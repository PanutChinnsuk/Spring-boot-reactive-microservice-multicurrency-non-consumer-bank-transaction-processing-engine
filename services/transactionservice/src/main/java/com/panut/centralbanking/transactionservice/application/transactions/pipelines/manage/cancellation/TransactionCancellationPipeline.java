package com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.cancellation;

import com.panut.centralbanking.transactionservice.api.service.account.AccountBalanceChangeServiceController;
import com.panut.centralbanking.transactionservice.api.service.account.AccountServiceController;
import com.panut.centralbanking.transactionservice.api.service.account.request.GetProcessedBalanceEventIdsRequest;
import com.panut.centralbanking.transactionservice.application.events.balancechange.BalanceChangeEventRepositoryManager;
import com.panut.centralbanking.transactionservice.application.events.balancechange.query.BalanceChangeEventQuery;
import com.panut.centralbanking.transactionservice.application.events.balancechange.service.BalanceChangeEventService;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.TransactionManagePipelineResult;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.repository.TransactionManageChain;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.repository.TransactionManagePipelineRepository;
import com.panut.centralbanking.transactionservice.application.transactions.transaction.TransactionRepositoryManager;
import com.panut.centralbanking.transactionservice.application.worker.WorkerService;
import com.panut.centralbanking.transactionservice.core.TransactionStatus;
import com.panut.centralbanking.transactionservice.core.WorkerName;
import com.panut.centralbanking.transactionservice.infrastructure.model.events.BalanceChangeEvent;
import com.panut.centralbanking.transactionservice.infrastructure.repository.BalanceChangeEventRepository;
import com.panut.centralbanking.transactionservice.infrastructure.repository.querydto.OptimizedBalanceChangeEvent;
import lombok.Getter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
class CancellationChain {
    private List<OptimizedBalanceChangeEvent> events;
    private List<String> eventIds;
    private Set<String> processedEventIds;

    private List<BalanceChangeEvent> reversing = new ArrayList<>();
    private List<String> directCancel = new ArrayList<>();


    public Mono<CancellationChain> events(final List<OptimizedBalanceChangeEvent> events) {
        return Mono.defer(() -> {
            this.events = events;
            eventIds = events.stream()
                    .filter(e -> e.getStatus()
                            .isPendingState())
                    .map(OptimizedBalanceChangeEvent::getId)
                    .toList();

            return Mono.just(this);
        });
    }

    public Mono<CancellationChain> processedEventIds(final Set<String> processedEventIds) {
        return Mono.defer(() -> {
            this.processedEventIds = processedEventIds;

            return Mono.just(this);
        });
    }

    public void addReversing(final BalanceChangeEvent balanceChangeEvent) {
        this.reversing.add(balanceChangeEvent);
    }

    public void addDirectCancel(final String eventId) {
        this.directCancel.add(eventId);
    }
}

@Component
public class TransactionCancellationPipeline {
    private final TransactionManagePipelineRepository repository;
    private final AccountBalanceChangeServiceController serviceController;
    private final TransactionRepositoryManager manager;
    private final BalanceChangeEventRepositoryManager eventManager;
    private final WorkerService workerService;
    private final BalanceChangeEventService eventService;
    private final BalanceChangeEventQuery eventQuery;
    private final BalanceChangeEventRepository eventRepository;


    public TransactionCancellationPipeline(TransactionManagePipelineRepository repository,
                                           AccountBalanceChangeServiceController serviceController ,
                                           TransactionRepositoryManager manager,
                                           BalanceChangeEventRepositoryManager eventManager,
                                           WorkerService workerService,
                                           BalanceChangeEventService eventService,
                                           BalanceChangeEventQuery eventQuery,
                                           BalanceChangeEventRepository eventRepository) {
        this.repository = repository;
        this.serviceController = serviceController;
        this.manager = manager;
        this.eventManager = eventManager;
        this.workerService = workerService;
        this.eventService = eventService;
        this.eventQuery = eventQuery;
        this.eventRepository = eventRepository;
    }

    public Mono<TransactionManageChain> clear(final TransactionManageChain manageChain,
                                              final String transactionId) {
        return eventQuery.getBalanceChangeEventsByTransactionId(transactionId)
                .flatMap(events ->
                    new CancellationChain().events(events)
                )
                .flatMap(chain ->
                        serviceController.getAppliableBalanceEvents(
                                new GetProcessedBalanceEventIdsRequest(chain.getEventIds())
                        )
                                .flatMap(chain::processedEventIds)
                )
                .map(chain -> {
                    var events = chain.getEvents();
                    var processed = chain.getProcessedEventIds();


                    events.forEach(event -> {
                        String eventId = event.getId();
                        if (processed.contains(eventId)) {
                            //here
                        }else {
                            chain.addDirectCancel(eventId);
                        }
                    });

                    return chain;
                })
                .thenReturn(manageChain);
    }

    public Mono<TransactionManagePipelineResult> cancel(final String transactionId) {
        return workerService.initWorker(WorkerName.TRANSACTION)
                .then(repository.fullTransaction(transactionId,
                        TransactionStatus.cancellableStatuses
                                .toArray(new TransactionStatus[0]))
                )
                .flatMap(chain -> clear(chain, transactionId))
                .map(TransactionManageChain::getTransaction)
                .flatMap(transaction ->
                        manager.save(transaction
                                .changeStatus(TransactionStatus.PENDING_CANCELLED)))
                .map(transaction -> new TransactionManagePipelineResult()
                        .transaction(transaction));

    }
}
