package com.panut.centralbanking.transactionservice.application.transactions.clearing.service;


import com.panut.centralbanking.appcore.integrationtest.SeededDatabaseIdFlag;
import com.panut.centralbanking.transactionservice.api.service.account.data.BalanceChangeData;
import com.panut.centralbanking.transactionservice.api.service.account.data.related.BalanceChangeType;
import com.panut.centralbanking.transactionservice.api.service.account.data.related.BalanceImpact;
import com.panut.centralbanking.transactionservice.application.transactions.clearing.TransactionBalanceClearingChain;
import com.panut.centralbanking.transactionservice.core.BalanceChangeEventStatus;
import com.panut.centralbanking.transactionservice.core.TransactionStatus;
import com.panut.centralbanking.transactionservice.infrastructure.model.events.BalanceChangeEvent;
import com.panut.centralbanking.transactionservice.infrastructure.repository.BalanceChangeEventRepository;
import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class BalanceChangeEventClearingQueryService {
    private final TransactionRepository transactionRepository;
    private final BalanceChangeEventRepository eventRepository;
    private final DatabaseClient databaseClient;

    public BalanceChangeEventClearingQueryService(TransactionRepository transactionRepository,
                                                  BalanceChangeEventRepository eventRepository,
                                                  DatabaseClient databaseClient) {
        this.transactionRepository = transactionRepository;
        this.eventRepository = eventRepository;
        this.databaseClient = databaseClient;
    }


    private final BalanceChangeEventStatus EXPECTING_EVENT_STATUS = BalanceChangeEventStatus.PENDING;

    public Mono<TransactionBalanceClearingChain> getSortedEachTransaction(final TransactionStatus status) {
        return databaseClient.sql("""
                SELECT t.id as t_id, e.* FROM transaction_balance_change_events e
                INNER JOIN transactions t
                ON t.id = e.transaction_id
                AND t.status = :transactionStatus
                WHERE e.status = :eventStatus
                FOR UPDATE SKIP LOCKED
                """)
                .bind("transactionStatus", status)
                .bind("eventStatus", EXPECTING_EVENT_STATUS)
                .map((row, meta) -> {
                    String transactionId = row.get("t_id", String.class);
                    String accountId = row.get("account_id", String.class);
                    BigDecimal amount = row.get("amount", BigDecimal.class);
                    BalanceChangeType balanceChangeType = BalanceChangeType.valueOf(row.get("balance_change_type", String.class));
                    BalanceImpact balanceImpact = BalanceImpact.valueOf(row.get("balance_impact", String.class));
                    LocalDateTime createdAt = row.get("created_at", LocalDateTime.class);

                    BalanceChangeEvent event = new BalanceChangeEvent()
                            .balanceChangeEvent(accountId, amount, balanceImpact,
                                    balanceChangeType, transactionId);


                    log.info("Map raw row to event model.");
                    return event;

                })
                .all()
                .doOnNext(a -> log.info("after to balance change events (flux)"))
                .as(events -> TransactionBalanceClearingChain
                        .newChain(events))
                .doOnNext(a ->
                        log.info("created a transaction balance clearing chain with the events"))
                .flatMap(chain -> chain.getUnsorted()
                        .doOnNext(a -> log.info("get unsorted (previous events)"))
                        .concatMap(event -> {
                            var transactionId = event.getTransactionId();

                            log.info("pre appending event account id {}", event
                                    .getAccountId());
                            return chain.put(transactionId, event)
                                    .doOnNext(a ->
                                            log.info("appended event into sink, event account id {}",
                                                    a.getAccountId()));

                        })
                        .then(Mono.just(chain))

                )
                .flatMap(chain -> chain.closeSinks()
                        .thenReturn(chain))
                .doOnNext(a -> log.info("Closed sinks."));


    }


    public Flux<BalanceChangeData> getBulk(final BalanceChangeEventStatus eventStatus,
                                           final int sizeLimit,
                                           final LocalDateTime incrementedBlacklistExpiry) {
        return getBulkAndIncrementExpiry(eventStatus, incrementedBlacklistExpiry, sizeLimit,
                SeededDatabaseIdFlag.TEST);
    }

    public Flux<BalanceChangeData> getBulk(final BalanceChangeEventStatus eventStatus,
                                           final int sizeLimit,
                                           final LocalDateTime incrementedBlacklistExpiry,
                                           final String blacklistFlag) {
        return getBulkAndIncrementExpiry(eventStatus, incrementedBlacklistExpiry, sizeLimit,
                blacklistFlag);
    }

    public Mono<List<String>> apply(final BalanceChangeEventStatus eventStatus,
                                    final int applyingSizeLimit,
                                    final LocalDateTime incrementedApplyingExpiry) {
        return getApplyingAndIncrementExpiry(eventStatus, incrementedApplyingExpiry, applyingSizeLimit,
                SeededDatabaseIdFlag.TEST);
    }

    public Mono<List<String>> apply(final BalanceChangeEventStatus eventStatus,
                                    final int applyingSizeLimit,
                                    final LocalDateTime incrementedApplyingExpiry,
                                    final String blacklistFlag) {
        return getApplyingAndIncrementExpiry(eventStatus, incrementedApplyingExpiry, applyingSizeLimit,
                blacklistFlag);
    }

    private Flux<BalanceChangeData> getBulkAndIncrementExpiry(final BalanceChangeEventStatus eventStatus,
                                                              final LocalDateTime incrementedBlacklistExpiry,
                                                              final int sizeLimit,
                                                              final String blacklistFlag) {
        return eventRepository.findAllIdsForClearing(eventStatus, sizeLimit,
                        blacklistFlag, LocalDateTime.now())
                .collectList()
                .flatMap(ids -> {
                    log.info("event ids from getBulkAndIncrementExpiry before increment is empty? {}",
                            ids.isEmpty());
                    if (ids.isEmpty())
                        return Mono.just(ids);


                    return eventRepository
                            .incrementBlacklistExpiry(ids, incrementedBlacklistExpiry)
                            .thenReturn(ids);
                })
                .flatMapMany(ids -> {
                    log.info("event ids from getBulk after increment is empty? {}",
                            ids.isEmpty());
                    if (ids.isEmpty())
                        return Flux.empty();

                    return eventRepository.findAllForClearing(eventStatus, ids);
                });
    }


    private Mono<List<String>> getApplyingAndIncrementExpiry(final BalanceChangeEventStatus eventStatus,
                                               final LocalDateTime incrementedApplyingExpiry,
                                               final int sizeLimit,
                                               final String blacklistFlag) {
        return eventRepository.findAllIdsForApplying(eventStatus, sizeLimit,
                        blacklistFlag, LocalDateTime.now())
                .collectList()
                .flatMap(ids -> {
                    log.info("event ids from getApplyingAndIncrementExpiry before increment is empty? {}",
                            ids.isEmpty());
                    if (ids.isEmpty())
                        return Mono.empty();


                    eventRepository
                            .incrementApplyingExpiry(ids, incrementedApplyingExpiry)
                            .doOnNext(a -> log.info("incremented applying blacklist expiry"))
                            .subscribe();

                    return Mono.just(ids);
                });

    }




}
