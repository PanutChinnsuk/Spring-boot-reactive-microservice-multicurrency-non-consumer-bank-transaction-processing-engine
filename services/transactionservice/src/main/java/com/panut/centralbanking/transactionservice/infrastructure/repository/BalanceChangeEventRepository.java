package com.panut.centralbanking.transactionservice.infrastructure.repository;

import com.panut.centralbanking.transactionservice.api.service.account.data.BalanceChangeData;
import com.panut.centralbanking.transactionservice.core.BalanceChangeEventStatus;
import com.panut.centralbanking.transactionservice.infrastructure.model.events.BalanceChangeEvent;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collection;

public interface BalanceChangeEventRepository
        extends ReactiveCrudRepository<BalanceChangeEvent, String> {

    @Query("""
            SELECT e.id
            FROM transaction_balance_change_events e
            WHERE e.status = :eventStatus AND
            e.id NOT LIKE :excludedIdFlag AND
            (e.blacklist_expires_at IS NULL OR e.blacklist_expires_at < :currentTimestamp)
            ORDER BY e.created_at ASC, e.transaction_id ASC
            LIMIT :sizeLimit FOR UPDATE
            """)
    Flux<String> findAllIdsForClearing(@Param("eventStatus")
                                               BalanceChangeEventStatus eventStatus,
                                               @Param("sizeLimit") int sizeLimit,
                                               @Param("excludedIdFlag") String excludedIdFlag,
                                       @Param("currentTimestamp") LocalDateTime currentTimestamp);



    @Query("""
            SELECT e.id AS event_id, e.account_id, e.amount, e.balance_change_type, e.balance_impact,
            e.created_at, e.transaction_id, e.blacklist_expires_at AS blacklist_expiry
            FROM transaction_balance_change_events e
            WHERE e.status = :eventStatus AND
            e.id IN (:eventIds)
            ORDER BY e.created_at ASC, e.transaction_id ASC 
            """)
    Flux<BalanceChangeData> findAllForClearing(@Param("eventStatus")
                                                BalanceChangeEventStatus eventStatus,
                                               @Param("eventIds") Collection<String> eventIds);

    @Modifying
    @Query("""
            UPDATE transaction_balance_change_events e 
            SET e.blacklist_expires_at = :incremented 
            WHERE e.id IN (:eventIds) 
            """)
    Mono<Void> incrementBlacklistExpiry(@Param("eventIds")Collection<String> eventIds,
                                        @Param("incremented")LocalDateTime incremented);



}
