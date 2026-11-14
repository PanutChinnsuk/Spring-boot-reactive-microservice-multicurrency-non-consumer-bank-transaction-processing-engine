package com.panut.centralbanking.transactionservice.application.transactions.clearing.schedules.modules;

import com.panut.centralbanking.appcore.integrationtest.SeededDatabaseIdFlag;
import com.panut.centralbanking.transactionservice.api.service.account.AccountBalanceChangeServiceController;
import com.panut.centralbanking.transactionservice.api.service.account.request.GetProcessedBalanceEventIdsRequest;
import com.panut.centralbanking.transactionservice.application.property.TransactionClearingProperty;
import com.panut.centralbanking.transactionservice.application.transactions.clearing.service.BalanceChangeEventClearingQueryService;
import com.panut.centralbanking.transactionservice.core.BalanceChangeEventStatus;
import com.panut.centralbanking.transactionservice.infrastructure.repository.BalanceChangeEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
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

    public BalanceChangeEventStatusClearingModuleV1(BalanceChangeEventClearingQueryService queryService,
                                                    BalanceChangeEventRepository repository,
                                                    TransactionClearingProperty property,
                                                    AccountBalanceChangeServiceController serviceController) {
        this.queryService = queryService;
        this.repository = repository;
        this.property = property;
        this.serviceController = serviceController;
    }



    private Disposable clear(final List<String> pendingIds) {
        return Mono.defer(() -> {
            log.info("before calling serviceController.getAppliableBalanceEvents");
            return serviceController.getAppliableBalanceEvents(
                            new GetProcessedBalanceEventIdsRequest(pendingIds))
                    .doOnNext(a ->
                            log.info("Cleared balance change event ids size: {}", a.size()))
                    .map(clearedIds -> {
                        if (clearedIds == null || clearedIds.isEmpty()) {
                            log.info("cleared ids is null ? {}, is empty ? {}",
                                    clearedIds == null, clearedIds.isEmpty());
                            return Mono.empty().subscribe();
                        }
                        log.info("clearedIds is valid, bulk updating status.");

                        return repository.bulkStatusUpdate(clearedIds,
                                        BalanceChangeEventStatus.COMPLETED)
                                .doOnNext(a ->
                                        log.info("Bulk set balance change event status to completed."))
                                .subscribe();
                    });
        }).subscribe();


    }

    public Mono<Void> clearPendings() {
        log.info("BalanceChangeEventStatusClearingModuleV1 before queryService");

        return queryService.apply(BalanceChangeEventStatus.PENDING,
                        property.getApplyingSizeLimit(),
                        property.getNewApplyingBlacklistExpiry(),
                        SeededDatabaseIdFlag.NONE)
                .doOnNext(a -> log.info("BalanceChangeEventStatusClearingModuleV1 after queryService"))
                .map(list -> {
                    return clear(list);
                })
                .then();


    }
}
