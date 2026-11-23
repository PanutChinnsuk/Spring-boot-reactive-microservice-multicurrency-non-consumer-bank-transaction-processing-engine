package com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.cancellation;

import com.panut.centralbanking.appR2dbccore.data.MapListSorter;
import com.panut.centralbanking.appcore.exceptions.SystemMisused;
import com.panut.centralbanking.appsecuritycore.servicesecurity.filter.MyServiceSignatureFilter;
import com.panut.centralbanking.transactionservice.api.service.account.AccountBalanceChangeServiceController;
import com.panut.centralbanking.transactionservice.api.service.account.request.GetProcessedBalanceEventIdsRequest;
import com.panut.centralbanking.transactionservice.application.events.balancechange.BalanceChangeEventRepositoryManager;
import com.panut.centralbanking.transactionservice.application.events.balancechange.ChangingBalance;
import com.panut.centralbanking.transactionservice.application.events.balancechange.query.BalanceChangeEventQuery;
import com.panut.centralbanking.transactionservice.application.events.balancechange.service.BalanceChangeEventService;
import com.panut.centralbanking.transactionservice.application.transactions.contents.overdraft.query.TransactionOverdraftContentQuery;
import com.panut.centralbanking.transactionservice.application.transactions.contents.overdraft.service.TransactionOverdraftContentService;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.TransactionManagePipelineResult;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.repository.TransactionManageChain;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.repository.TransactionManagePipelineRepository;
import com.panut.centralbanking.transactionservice.application.transactions.transaction.TransactionRepositoryManager;
import com.panut.centralbanking.transactionservice.application.transactions.transaction.query.TransactionQuery;
import com.panut.centralbanking.transactionservice.application.worker.WorkerService;
import com.panut.centralbanking.transactionservice.core.BalanceChangeEventStatus;
import com.panut.centralbanking.transactionservice.core.TransactionStatus;
import com.panut.centralbanking.transactionservice.core.WorkerName;
import com.panut.centralbanking.transactionservice.infrastructure.model.events.BalanceChangeEvent;
import com.panut.centralbanking.transactionservice.infrastructure.repository.BalanceChangeEventRepository;
import com.panut.centralbanking.transactionservice.infrastructure.repository.querydto.OptimizedBalanceChangeEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.*;

record ProcLedgerResult(BigDecimal leftOver,
                                 List<BalanceChangeEvent> newEventS) {}

@Slf4j
@Getter
class CancellationChain {
    private Map<String, List<OptimizedBalanceChangeEvent>> eventMap = new HashMap<>();
    private List<String> eventIds = new ArrayList<>();
    private Set<String> processedEventIds;

    private Map<String, List<String>> possibleOverdraftContents;

    private List<BalanceChangeEvent> reversing = new ArrayList<>();
    private List<String> directCancel = new ArrayList<>();

    private boolean requiredTransactionOverdraftContext = false;


    public Mono<CancellationChain> eventMap(final List<OptimizedBalanceChangeEvent> events) {
        return Mono.defer(() -> {
            events.forEach(event -> {
                String key = event.getAccountId();

                MapListSorter.sortMapList(eventMap,
                        key, event);

                eventIds.add(event.getId());

                if (event.getBalanceChangeType().requiredTransactionOverdraftContent())
                    this.requiredTransactionOverdraftContext = true;
            });

            log.info("after sortMapList, eventMap size: {}",
                    eventMap.size());

            return Mono.just(this);
        });
    }

    public Mono<CancellationChain> processedEventIds(final Set<String> processedEventIds) {
        return Mono.defer(() -> {
            this.processedEventIds = processedEventIds;

            return Mono.just(this);
        });
    }

    public Mono<CancellationChain> possibleOverdraftContents(
            final Map<String, List<String>> possibleOverdraftContents) {
        return Mono.defer(() -> {
            this.possibleOverdraftContents = possibleOverdraftContents;

            return Mono.just(this);
        });
    }

    public void addReversing(final List<BalanceChangeEvent> balanceChangeEvents) {
        if (balanceChangeEvents == null)
            return;

        balanceChangeEvents.forEach(event -> {
            log.info("EVENT ADDED, TYPE {}, IMPACT {}, AMOUNT {}",
                    event.getBalanceChangeType(),
                    event.getBalanceImpact(),
                    event.getAmount());
        });

        this.reversing.addAll(balanceChangeEvents);
    }

    public void addDirectCancel(final String eventId) {
        this.directCancel.add(eventId);
    }
}

@Slf4j
@Component
public class TransactionCancellationPipeline {
    private final TransactionManagePipelineRepository repository;
    private final AccountBalanceChangeServiceController serviceController;
    private final TransactionRepositoryManager manager;
    private final WorkerService workerService;
    private final BalanceChangeEventService eventService;
    private final BalanceChangeEventQuery eventQuery;
    private final BalanceChangeEventRepository eventRepository;
    private final TransactionOverdraftContentQuery overdraftContentQuery;
    private final TransactionQuery transactionQuery;



    public TransactionCancellationPipeline(TransactionManagePipelineRepository repository,
                                           AccountBalanceChangeServiceController serviceController ,
                                           TransactionRepositoryManager manager,
                                           WorkerService workerService,
                                           BalanceChangeEventService eventService,
                                           BalanceChangeEventQuery eventQuery,
                                           BalanceChangeEventRepository eventRepository,
                                           TransactionOverdraftContentQuery overdraftContentQuery,
                                           TransactionQuery transactionQuery) {
        this.repository = repository;
        this.serviceController = serviceController;
        this.manager = manager;
        this.workerService = workerService;
        this.eventService = eventService;
        this.eventQuery = eventQuery;
        this.eventRepository = eventRepository;
        this.overdraftContentQuery = overdraftContentQuery;
        this.transactionQuery = transactionQuery;
    }

    private ProcLedgerResult clearOverdrafted(final BigDecimal overdraftAdded,
                                          final BigDecimal overdraftDeducted,
                                          final String accountId,
                                          final String transactionId) {
        List<BalanceChangeEvent> newEvents = new ArrayList<>();

        var hasAddedOverdraft = overdraftAdded.signum() > 0;
        var hasDeductedOverdraft = overdraftDeducted.signum() > 0;

        if (hasAddedOverdraft) {
            log.info("hasAddedOverdraft");
            newEvents.add(eventService.create(accountId, overdraftAdded, transactionId,
                    ChangingBalance.overdraftedDeduct()));
        }

        if (hasDeductedOverdraft) {
            log.info("hasDeductedOverdraft");
            newEvents.add(eventService.create(accountId, overdraftDeducted, transactionId,
                    ChangingBalance.overdraftedAdd()));
        }


        return new ProcLedgerResult(BigDecimal.ZERO, newEvents);
    }

    private ProcLedgerResult clearBalance(final BigDecimal balanceAdded,
                                          final BigDecimal balanceDeducted,
                                          final String accountId,
                                          final String transactionId) {
        List<BalanceChangeEvent> newEvents = new ArrayList<>();

        var balanceReceived = balanceAdded.signum() > 0;
        var noBalanceDeducted = balanceDeducted.signum() <= 0;
        var hasBalanceDeducted = balanceDeducted.signum() > 0;

        if (balanceReceived) {
            log.info("balanceReceived");
            newEvents.add(eventService.create(accountId, balanceAdded, transactionId,
                    ChangingBalance.balanceDeduct()));
        }

        if (noBalanceDeducted) {
            log.info("noBalanceDeducted");
            return new ProcLedgerResult(BigDecimal.ZERO, newEvents);
        }

        if (hasBalanceDeducted) {
            log.info("hasBalanceDeducted");
            newEvents.add(eventService.create(accountId, balanceDeducted, transactionId,
                    ChangingBalance.balanceAdd()));
            return new ProcLedgerResult(BigDecimal.ZERO, newEvents);
        }

        throw new SystemMisused("Cancellation reversing proc balance ledger, impossible condition.");
    }

    private ProcLedgerResult clearReserve(final BigDecimal reserveAdded,
                                          final BigDecimal reserveDeducted,
                                          final String accountId,
                                          final String transactionId) {
        List<BalanceChangeEvent> newEvents = new ArrayList<>();

        var bothAreZero = reserveAdded.signum() <= 0 &&
                reserveDeducted.signum() <= 0;
        var onlyDeducted = reserveAdded.signum() <= 0 && reserveDeducted.signum() > 0;
        var onlyAdded = reserveAdded.signum() > 0 && reserveDeducted.signum() <= 0;
        var addedAndDeducted = reserveAdded.compareTo(reserveDeducted) >= 0 &&
                reserveDeducted.signum() > 0;
        var addedLessThanDeducted = reserveAdded.compareTo(reserveDeducted) < 0 &&
                reserveDeducted.signum() > 0;

        if (bothAreZero)
            return new ProcLedgerResult(BigDecimal.ZERO, null);

        //RARE CASE SAFE GUARD
        if (onlyDeducted) {
            log.info("onlyDeducted");
            newEvents.add(eventService.create(accountId, reserveDeducted, transactionId,
                    ChangingBalance.balanceAdd()));

            return new ProcLedgerResult(reserveDeducted, newEvents);
        }

        if (onlyAdded) {
            log.info("onlyAdded");
            newEvents.add(eventService.create(accountId, reserveAdded, transactionId,
                    ChangingBalance.reserveDeduct()));

            return new ProcLedgerResult(BigDecimal.ZERO, newEvents);
        }

        if (addedAndDeducted) {
            log.info("addedAndDeducted");
            BigDecimal leftOver = reserveAdded.subtract(reserveDeducted);
            log.info("leftOver: {}", leftOver);

            if (leftOver.signum() <= 0) {
                BigDecimal instantRefund = reserveDeducted;

                log.info("leftOver is zero due to reserveDeducted has the same amount, refund {}",
                        instantRefund);

                newEvents.add(eventService.create(accountId, instantRefund, transactionId,
                        ChangingBalance.balanceAdd()));
            }else {
                BigDecimal instantRefund = reserveAdded.subtract(reserveDeducted);

                log.info("leftOver is not zero due to reserves not fully settled, refund {}",
                        instantRefund);

                newEvents.add(eventService.create(accountId, instantRefund, transactionId,
                        ChangingBalance.balanceAdd()));
                newEvents.add(eventService.create(accountId, leftOver, transactionId,
                        ChangingBalance.reserveDeduct()));
            }

            return new ProcLedgerResult(leftOver, newEvents);
        }

        //RARE CASE SAFE GUARD
        if (addedLessThanDeducted) {
            log.info("addedLessThanDeducted");
            newEvents.add(eventService.create(accountId, reserveDeducted, transactionId,
                    ChangingBalance.balanceAdd()));

            if (reserveAdded.signum() > 0) {
                log.info("unsettled reserveAdded: {}", reserveAdded);


                newEvents.add(eventService.create(accountId, reserveAdded, transactionId,
                        ChangingBalance.reserveDeduct()));
            }

            return new ProcLedgerResult(reserveDeducted, newEvents);
        }

        throw new SystemMisused("Cancellation reversing proc reserve ledger, impossible condition.");
    }



    private Mono<TransactionManageChain> clear(final TransactionManageChain manageChain,
                                              final String transactionId) {
        return eventQuery.getBalanceChangeEventsByTransactionId(transactionId)
                .doOnNext(a ->
                        log.info("get event of transaction to cancel"))
                .flatMap(events ->
                    new CancellationChain().eventMap(events)
                )
                .doOnNext(a -> log.info("started a cancellation chain"))
                .flatMap(chain ->
                        serviceController.getAppliableBalanceEvents(
                                new GetProcessedBalanceEventIdsRequest(chain.getEventIds())
                        ).flatMap(chain::processedEventIds)
                )
                .doOnNext(a -> log.info("get processed event ids"))
                .map(chain -> {
                    var eventMap = chain.getEventMap();
                    var processed = chain.getProcessedEventIds();
                    var possibleOverdraftContents = chain.getPossibleOverdraftContents();


                    eventMap.forEach((accountId,
                                      events) -> {

                        var reserveDeducted = BigDecimal.ZERO;
                        var reserveAdded = BigDecimal.ZERO;
                        var balanceDeducted = BigDecimal.ZERO;
                        var balanceAdded = BigDecimal.ZERO;
                        var overdraftDeducted = BigDecimal.ZERO;
                        var overdraftAdded = BigDecimal.ZERO;

                        for (int i = 0; i < events.size(); i++) {
                            var event = events.get(i);
                            String eventId = event.getId();

                            if (!processed.contains(eventId)) {
                                log.info("processed ids does not contain event id {}, going straight to direct cancel",
                                        eventId);
                                chain.addDirectCancel(eventId);
                                continue;
                            }

                            var isAdd = event.getBalanceImpact().ADD();
                            var isDeduct = event.getBalanceImpact().DEDUCT();
                            var type = event.getBalanceChangeType();
                            var amount = event.getAmount();

                            log.info("processed event id, {} :balanceChangeType: {}, balanceImpact: {}",
                                    event.getId(),
                                    type,
                                    event.getBalanceImpact());

                            if (type.BALANCE()) {
                                if (isAdd)
                                    balanceAdded = balanceAdded.add(amount);
                                if (isDeduct)
                                    balanceDeducted = balanceDeducted.add(amount);
                            }

                            if (type.RESERVE()) {
                                if (isAdd)
                                    reserveAdded = reserveAdded.add(amount);
                                if (isDeduct)
                                    reserveDeducted = reserveDeducted.add(amount);
                            }

                            if (type.requiredTransactionOverdraftContent()) {
                                if (isAdd)
                                    overdraftAdded = overdraftAdded.add(amount);
                                if (isDeduct)
                                    overdraftDeducted = overdraftDeducted.add(amount);
                            }

                        }

                        var clearedReserveResult = clearReserve(reserveAdded,
                                reserveDeducted, accountId, transactionId);

                        log.info("account {}, before balanceDeducted: {}",
                                accountId, balanceDeducted);
                        balanceDeducted = balanceDeducted.subtract(
                                clearedReserveResult.leftOver());
                        log.info("account {}, after balanceDeducted: {}, subtracted leftOver: {}",
                                accountId, balanceDeducted, clearedReserveResult.leftOver());

                        var clearedBalanceResult = clearBalance(balanceAdded,
                                balanceDeducted, accountId, transactionId);

                        var clearedOverdraftResult = clearOverdrafted(overdraftAdded,
                                overdraftDeducted, accountId, transactionId);

                        chain.addReversing(clearedReserveResult.newEventS());
                        chain.addReversing(clearedBalanceResult.newEventS());
                        chain.addReversing(clearedOverdraftResult.newEventS());

                        log.info("new events created: {}", chain.getReversing().size());
                    });

                    return chain;
                })
                .doOnNext(a -> log.info("performing bulk"))
                .flatMap(chain -> {
                    return eventService.flush(chain.getReversing())
                            .doOnNext(a ->
                                    log.info("flush all new events, size {}",
                                            chain.getReversing().size()))
                            .then(Mono.just(chain));
                        })
                .flatMap(chain -> {
                      if (chain.getDirectCancel().isEmpty())
                          return Mono.empty();

                      return eventRepository
                              .bulkStatusUpdate(chain.getDirectCancel(),
                                      BalanceChangeEventStatus.CANCELLED)
                              .doOnNext(a -> log.info("set direct cancel statuses to cancelled"));
                })
                .thenReturn(manageChain);
    }


    
    //self reminder >>> make it to only query just for the transaction itself IF
    //future does NOT required its profile
    public Mono<TransactionManagePipelineResult> cancel(final String transactionId) {
        return workerService.initWorker(WorkerName.TRANSACTION)
                .then(transactionQuery.getReversed(transactionId))
                .expand(nested ->
                    manager.save(nested
                            .changeStatus(TransactionStatus.CANCELLED))
                            .doOnNext(a -> log.info("nested reversed id, {}",
                                    nested.getId()))
                            .flatMap(saved ->
                                    transactionQuery.getReversed(saved.getId()))
                )
                .then(repository.justTransaction(transactionId,
                        TransactionStatus.cancellableStatuses
                                .toArray(new TransactionStatus[0]))
                )
                .doOnNext(a ->
                        log.info("after get just transaction and its profile"))
                .flatMap(chain -> clear(chain, transactionId))
                .map(TransactionManageChain::getTransaction)
                .flatMap(transaction ->
                        manager.save(transaction
                                .changeStatus(TransactionStatus.PENDING_CANCELLED)))
                .doOnNext(a -> log.info("change transaction status to PENDING_CANCELLED"))
                .map(transaction -> new TransactionManagePipelineResult()
                        .transaction(transaction))
                .doOnSuccess(a ->
                        log.info("Successfully cancelled transaction id of {}",
                                transactionId))
                .doOnError(error ->
                        log.error("Failed to cancel transaction, {}",
                                error.getLocalizedMessage()));

    }
}
