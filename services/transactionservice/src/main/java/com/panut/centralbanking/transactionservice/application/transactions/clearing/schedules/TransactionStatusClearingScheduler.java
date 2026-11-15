package com.panut.centralbanking.transactionservice.application.transactions.clearing.schedules;


import com.panut.centralbanking.appcore.integrationtest.SeededDatabase;
import com.panut.centralbanking.appcore.integrationtest.SeededDatabaseIdFlag;
import com.panut.centralbanking.transactionservice.application.property.TransactionClearingProperty;
import com.panut.centralbanking.transactionservice.application.transactions.clearing.schedules.modules.TransactionStatusClearingModuleV1;
import com.panut.centralbanking.transactionservice.core.BalanceChangeEventStatus;
import com.panut.centralbanking.transactionservice.core.TransactionStatus;
import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class TransactionStatusClearingScheduler {
    private final TransactionStatusClearingModuleV1 v1Module;

    public TransactionStatusClearingScheduler(TransactionStatusClearingModuleV1 v1Module) {
        this.v1Module = v1Module;
    }


    @Scheduled(fixedDelayString = "#{(60000 * @transactionClearingProperty.getScheduleMinutes) + " +
            "@transactionClearingProperty.getScheduleMilliOffset}", initialDelay = 20000L)
    public Mono<Void> clearPendingAuthorize() {
        v1Module.clear(TransactionStatus.PENDING_AUTHORIZED,
                SeededDatabaseIdFlag.TEST).subscribe();

        return Mono.empty();


    }

    @Scheduled(fixedDelayString = "#{(120000 * @transactionClearingProperty.getScheduleMinutes) + " +
            "@transactionClearingProperty.getScheduleMilliOffset}", initialDelay = 40000L)
    public Mono<Void>  clearPendingPosted() {
        v1Module.clear(TransactionStatus.PENDING_POSTED,
                SeededDatabaseIdFlag.TEST).subscribe();

        return Mono.empty();
    }

    @Scheduled(fixedDelayString = "#{(180000 * @transactionClearingProperty.getScheduleMinutes) + " +
            "@transactionClearingProperty.getScheduleMilliOffset}", initialDelay = 60000L)
    public Mono<Void>  clearPendingReverse() {
        v1Module.clear(TransactionStatus.PENDING_REVERSED,
                SeededDatabaseIdFlag.TEST).subscribe();

        return Mono.empty();
    }
}
