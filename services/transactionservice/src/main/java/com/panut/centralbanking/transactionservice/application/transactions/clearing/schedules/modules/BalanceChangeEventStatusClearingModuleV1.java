package com.panut.centralbanking.transactionservice.application.transactions.clearing.schedules.modules;

import com.panut.centralbanking.appcore.integrationtest.SeededDatabaseIdFlag;
import com.panut.centralbanking.transactionservice.api.service.account.AccountBalanceChangeServiceController;
import com.panut.centralbanking.transactionservice.api.service.account.request.GetProcessedBalanceEventIdsRequest;
import com.panut.centralbanking.transactionservice.application.property.TransactionClearingProperty;
import com.panut.centralbanking.transactionservice.application.transactions.clearing.service.BalanceChangeEventClearingQueryService;
import com.panut.centralbanking.transactionservice.application.worker.WorkerService;
import com.panut.centralbanking.transactionservice.core.BalanceChangeEventStatus;
import com.panut.centralbanking.transactionservice.core.WorkerName;
import com.panut.centralbanking.transactionservice.infrastructure.repository.BalanceChangeEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
public class BalanceChangeEventStatusClearingModuleV1 {
    private final BalanceChangeEventClearingQueryService queryService;
    private final BalanceChangeEventRepository repository;
    private final TransactionClearingProperty property;
    private final AccountBalanceChangeServiceController serviceController;
    private final WorkerService workerService;

    public BalanceChangeEventStatusClearingModuleV1(BalanceChangeEventClearingQueryService queryService,
                                                    BalanceChangeEventRepository repository,
                                                    TransactionClearingProperty property,
                                                    AccountBalanceChangeServiceController serviceController,
                                                    WorkerService workerService) {
        this.queryService = queryService;
        this.repository = repository;
        this.property = property;
        this.serviceController = serviceController;
        this.workerService = workerService;
    }


    public Mono<Void> bulkUpdate(final Collection<String> processedIds) {
        return repository.bulkStatusUpdate(processedIds,
                        BalanceChangeEventStatus.COMPLETED)
                .doOnSuccess(a ->
                        log.info("Bulk set balance change event status to completed."));
    }

    public Mono<Void> bulkUpdateWithWorker(final Collection<String> processedIds) {
        return workerService.initWorker(WorkerName.TRANSACTION)
                .then(bulkUpdate(processedIds));
    }


    private Mono<Void> clear(final List<String> pendingIds) {
        log.info("before calling serviceController.getAppliableBalanceEvents");
        return serviceController.getAppliableBalanceEvents(
                        new GetProcessedBalanceEventIdsRequest(pendingIds))
                .doOnNext(a ->
                        log.info("Cleared balance change event ids size: {}", a.size()))
                .flatMap(processedIds -> {
                    if (processedIds == null || processedIds.isEmpty()) {
                        log.info("processedIds ids is null ? {}, is empty ? {}",
                                processedIds == null, processedIds.isEmpty());
                        return Mono.empty();
                    }
                    log.info("processedIds is valid, bulk updating status.");

                    return bulkUpdate(processedIds);
                });


    }

    public Mono<Void> clearPendings() {
        log.info("BalanceChangeEventStatusClearingModuleV1 before queryService");

        return queryService.apply(BalanceChangeEventStatus.PENDING,
                        property.getApplyingSizeLimit(),
                        property.getNewApplyingBlacklistExpiry(),
                        SeededDatabaseIdFlag.TEST)
                .doOnNext(a -> log.info("BalanceChangeEventStatusClearingModuleV1 after queryService"))
                .flatMap(list -> clear(list))
                .then();


    }
}
