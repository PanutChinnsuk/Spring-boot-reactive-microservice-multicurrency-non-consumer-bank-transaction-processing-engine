package com.panut.centralbanking.transactionservice.testrepository;

import com.panut.centralbanking.transactionservice.core.BalanceChangeEventStatus;
import com.panut.centralbanking.transactionservice.infrastructure.model.events.BalanceChangeEvent;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface BalanceChangeEventTestRepository
        extends ReactiveCrudRepository<BalanceChangeEvent, String> {


    Mono<Void> deleteByTransactionId(String transactionId);

    Flux<BalanceChangeEvent> findByTransactionId(String transactionId);


    @Query("""
            UPDATE transaction_balance_change_events e SET e.status = :eventStatus
            WHERE e.transaction_id IN (:transactionIds)
            """)
    Mono<Void> setAllStatus(@Param("transactionIds")Set<String> ids,
                             @Param("eventStatus")BalanceChangeEventStatus eventStatus);


}
