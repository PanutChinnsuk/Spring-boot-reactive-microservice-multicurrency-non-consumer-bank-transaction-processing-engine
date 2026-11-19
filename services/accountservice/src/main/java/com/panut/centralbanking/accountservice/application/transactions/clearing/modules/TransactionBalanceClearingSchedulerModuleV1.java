package com.panut.centralbanking.accountservice.application.transactions.clearing.modules;

import com.panut.centralbanking.accountservice.api.data.BalanceChangeData;
import com.panut.centralbanking.accountservice.api.service.transaction.TransactionClearingServiceController;
import com.panut.centralbanking.accountservice.application.transactions.accountbalancechange.service.BalanceChangeService;
import com.panut.centralbanking.accountservice.application.worker.WorkerService;
import com.panut.centralbanking.accountservice.core.WorkerName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class TransactionBalanceClearingSchedulerModuleV1 {
    private final BalanceChangeService balanceChangeService;
    private final TransactionClearingServiceController serviceController;
    private final WorkerService workerService;

    public TransactionBalanceClearingSchedulerModuleV1(BalanceChangeService balanceChangeService,
                                               TransactionClearingServiceController serviceController,
                                                       WorkerService workerService) {
        this.balanceChangeService = balanceChangeService;
        this.serviceController = serviceController;
        this.workerService = workerService;
    }

    private String name = "TransactionBalanceClearingScheduler";

    private Mono<Void> process(final List<BalanceChangeData> datas) {
        log.info("{} : pre process", name);
        return balanceChangeService.bulk(datas, false)
                .doOnNext(a -> log.info("{} : post process", name))
                .then();
    }

    public Mono<Void> clearPending() {
        return workerService.initWorker(WorkerName.TRANSACTION)
                .thenMany(serviceController.getBulkPending())
                .collectList()
                .doOnNext(a -> log.info("{} : bulkPending to list",
                        name))
                .flatMap(list -> {
                    if (list.isEmpty()) {
                        log.info("event list from transaction service to clear bulk in " +
                                "TransactionBalanceClearingScheduleModulbeV1 is empty, " +
                                "returning empty mono.");
                        return Mono.empty();
                    }


                    log.info("before calling process");
                    return process(list)
                            .doOnNext(a ->
                                    log.info("{} : after processed", name));
                });


    }
}
