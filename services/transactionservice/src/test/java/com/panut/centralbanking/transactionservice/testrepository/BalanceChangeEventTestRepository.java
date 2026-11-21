package com.panut.centralbanking.transactionservice.testrepository;

import com.panut.centralbanking.transactionservice.core.BalanceChangeEventStatus;
import com.panut.centralbanking.transactionservice.infrastructure.model.events.BalanceChangeEvent;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collection;

public interface BalanceChangeEventTestRepository
        extends ReactiveCrudRepository<BalanceChangeEvent, String> {


    Mono<Void> deleteByTransactionId(String transactionId);

    Flux<BalanceChangeEvent> findByTransactionId(String transactionId);

    Flux<BalanceChangeEvent> findByTransactionIdIn(Collection<String> transactionIds);

    @Query("""
            SELECT e.id FROM transaction_balance_change_events e
            WHERE e.transaction_id IN (:transactionIds) AND
            e.id NOT LIKE :excludedIdFlag
            """)
    Flux<String> getNewCreated(@Param("transactionIds") Collection<String> transactionIds,
                               @Param("excludedIdFlag") String excludedIdFlag);

    Mono<Void> deleteByIdIn(Collection<String> ids);

    @Query("""
            UPDATE transaction_balance_change_events e SET e.status = :eventStatus
            WHERE e.transaction_id IN (:transactionIds)
            """)
    Mono<Void> setAllStatusWithTransactionIds(@Param("transactionIds")Collection<String> ids,
                                              @Param("eventStatus")BalanceChangeEventStatus eventStatus);

    @Query("""
            UPDATE transaction_balance_change_events e SET e.status = :eventStatus
            WHERE e.id IN (:ids)
            """)
    Mono<Void> setAllStatusWithEventIds(@Param("ids")Collection<String> ids,
                            @Param("eventStatus")BalanceChangeEventStatus eventStatus);


    @Query("""
            UPDATE transaction_balance_change_events e SET e.blacklist_expires_at = :now,
            e.applying_expires_at = :now
            WHERE e.id IN (:eventIds)
            """)
    Mono<Void> setAllBlacklistExpirty(@Param("eventIds")Collection<String> eventIds,
                                      @Param("now")LocalDateTime now);



    @Query("""
            UPDATE transaction_balance_change_events e SET
            e.status = :toStatus
            WHERE e.id = :eventId
            """)
    Mono<Void> rollbackStatus(@Param("eventId") String eventId,
                              @Param("toStatus") BalanceChangeEventStatus toStatus);


    Flux<BalanceChangeEvent> findByIdIn(Collection<String> eventIds);




}
