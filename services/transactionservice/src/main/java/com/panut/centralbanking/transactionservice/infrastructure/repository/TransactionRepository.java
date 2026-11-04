package com.panut.centralbanking.transactionservice.infrastructure.repository;

import com.panut.centralbanking.transactionservice.core.TransactionStatus;
import com.panut.centralbanking.transactionservice.infrastructure.model.Transaction;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

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
}
