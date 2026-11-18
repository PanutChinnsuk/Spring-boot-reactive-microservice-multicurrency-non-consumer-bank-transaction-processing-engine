package com.panut.centralbanking.accountservice.infrastructure.repository;

import com.panut.centralbanking.accountservice.core.AccountStatus;
import com.panut.centralbanking.accountservice.infrastructure.model.Account;
import com.panut.centralbanking.accountservice.infrastructure.model.events.ProcessedBalanceChangeEvent;
import com.panut.centralbanking.accountservice.infrastructure.repository.queryresult.AccountInfoForTransactionQueryResult;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.Set;

public interface ProcessedBalanceChangeEventRepository
        extends ReactiveCrudRepository<ProcessedBalanceChangeEvent, String> {


    @Query("""
            SELECT pe.event_id FROM account_processed_balance_change_events pe 
            WHERE pe.event_id IN (:eventIds)
            """)
    Flux<String> getProcessed(@Param("eventIds")Collection<String> eventIds);

    @Query("""
            SELECT pe.event_id FROM account_processed_balance_change_events pe 
            WHERE pe.event_id IN (:eventIds)
            """)
    Flux<String> getAppliable(@Param("eventIds")Collection<String> eventIds);

}
