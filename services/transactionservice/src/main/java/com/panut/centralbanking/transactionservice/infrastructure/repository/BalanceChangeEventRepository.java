package com.panut.centralbanking.transactionservice.infrastructure.repository;

import com.panut.centralbanking.transactionservice.api.service.account.data.BalanceChangeData;
import com.panut.centralbanking.transactionservice.core.BalanceChangeEventStatus;
import com.panut.centralbanking.transactionservice.infrastructure.model.events.BalanceChangeEvent;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface BalanceChangeEventRepository
        extends ReactiveCrudRepository<BalanceChangeEvent, String> {


    @Query("""
            SELECT e.account_id, e.amount, e.balance_change_type, e.balance_impact,
            e.created_at, e.transaction_id
            FROM transaction_balance_change_events e
            WHERE e.status = :eventStatus 
            GROUP BY e.transaction_id
            ORDER BY e.created_at ASC
            LIMIT :sizeLimit FOR UPDATE
            """)
    Flux<BalanceChangeData> findAllForClearing(@Param("eventStatus")
                                                BalanceChangeEventStatus eventStatus,
                                               @Param("sizeLimit") int sizeLimit);

}
