package com.panut.centralbanking.transactionservice.testrepository;

import com.panut.centralbanking.transactionservice.core.BalanceChangeEventStatus;
import com.panut.centralbanking.transactionservice.core.TransactionStatus;
import com.panut.centralbanking.transactionservice.infrastructure.model.Transaction;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

public interface TransactionTestRepository extends ReactiveCrudRepository<Transaction, String> {



    @Modifying
    @Query("""
            UPDATE transactions t SET t.status = :status
            WHERE t.id = :id
            """)
    Mono<Void> setStatus(@Param("status") TransactionStatus status,
                            @Param("id") String id);

    @Modifying
    @Query("""
            UPDATE transactions t SET t.status = :status
            WHERE t.id IN (:ids)
            """)
    Mono<Void> setStatus(@Param("status") TransactionStatus status,
                         @Param("ids") Collection<String> ids);

    Flux<Transaction> findByIdIn(Collection<String> ids);
}
