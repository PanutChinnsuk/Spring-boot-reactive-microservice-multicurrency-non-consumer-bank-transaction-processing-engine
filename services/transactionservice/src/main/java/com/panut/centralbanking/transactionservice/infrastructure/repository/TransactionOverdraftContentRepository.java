package com.panut.centralbanking.transactionservice.infrastructure.repository;


import com.panut.centralbanking.transactionservice.infrastructure.model.datacontent.TransactionOverdraftContent;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

public interface TransactionOverdraftContentRepository extends
        ReactiveCrudRepository<TransactionOverdraftContent, String> {



    @Query("""
            SELECT DISTINCT ct.* FROM transaction_overdraft_content ct
            JOIN transaction_balance_change_events e
            ON e.transaction_id = ct.transaction_id
            WHERE e.id IN (:eventIds)
            """)
    Flux<TransactionOverdraftContent> getOverdraftContext(@Param("eventIds")Collection<String> eventIds);
}
