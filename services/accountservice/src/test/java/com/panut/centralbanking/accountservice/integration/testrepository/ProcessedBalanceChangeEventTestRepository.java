package com.panut.centralbanking.accountservice.integration.testrepository;

import com.panut.centralbanking.accountservice.infrastructure.model.events.ProcessedBalanceChangeEvent;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

public interface ProcessedBalanceChangeEventTestRepository
        extends ReactiveCrudRepository<ProcessedBalanceChangeEvent, String> {


    @Query("""
            DELETE FROM account_processed_balance_change_events e
            WHERE e.event_id LIKE :deleteFlag
            """)
    Mono<Void> deleteByTestFlag(@Param("deleteFlag") String deleteFlag);

}
