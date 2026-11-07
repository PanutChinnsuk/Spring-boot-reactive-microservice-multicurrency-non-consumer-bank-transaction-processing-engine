package com.panut.centralbanking.transactionservice.infrastructure.repository;

import com.panut.centralbanking.transactionservice.core.BalanceChangeEventStatus;
import com.panut.centralbanking.transactionservice.core.TransactionStatus;
import com.panut.centralbanking.transactionservice.infrastructure.model.Transaction;
import com.panut.centralbanking.transactionservice.infrastructure.model.events.BalanceChangeEvent;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

public interface TransactionRepository extends ReactiveCrudRepository<Transaction, String> {

    @Query("""
            SELECT t.* FROM transactions t
            WHERE t.id = :transactionId AND
            t.status = :status
            FOR UPDATE
            """)
    Mono<Transaction> findTransactionById(@Param("transactionId") String transactionId,
                                          @Param("status")
                                          TransactionStatus expectedTransactionStatus);



    @Query("""
            SELECT t.id FROM transactions t
            WHERE t.status = :status
            FOR UPDATE SKIP LOCKED
            """)
    Flux<String> getTransactionIdsWithStatus(@Param("status") TransactionStatus status,
                                             @Param("sizeLimit") int sizeLimit);

    @Query("""
            SELECT t.id FROM transactions t
            WHERE t.status = :pendingStatus
            AND t.id NOT LIKE :excludedIdFlag
            AND NOT EXISTS (
                SELECT e.id FROM transaction_balance_change_events e
                WHERE e.status = :eventStatus 
                AND e.transaction_id = t.id
            )
            ORDER BY t.created_at ASC
            LIMIT :sizeLimit FOR UPDATE SKIP LOCKED
            """)
    Flux<String> getClearedTransactions(@Param("currentStatus") TransactionStatus pendingStatus,
                                   @Param("eventStatus") BalanceChangeEventStatus noneWithEventStatus,
                                   @Param("sizeLimit") int sizeLimit,
                                        @Param("excludedIdFlag") String excludedIdFlag);


    @Modifying
    @Query("""
            UPDATE transactions t SET t.status = :upNextStatus
            WHERE t.status = :currentStatus AND
            t.id IN (:transactionIds)
            """)
    Mono<Void> toNextStatus(@Param("upNextStatus") TransactionStatus upNextStatus,
                            @Param("currentStatus") TransactionStatus currentStatus,
                            @Param("transactionIds") List<String> transactionIds);
}
