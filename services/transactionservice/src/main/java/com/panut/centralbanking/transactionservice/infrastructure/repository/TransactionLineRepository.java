package com.panut.centralbanking.transactionservice.infrastructure.repository;

import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionLine;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface TransactionLineRepository extends ReactiveCrudRepository<TransactionLine, String> {


    @Query("""
            SELECT tl.* FROM transaction_lines tl
            WHERE tl.transaction_id = :transactionId
            FOR UPDATE
            """)
    Flux<TransactionLine> findTransactionLines(@Param("transactionId") String transactionId);

}
