package com.panut.centralbanking.accountservice.application.transactions.clearing;

import com.panut.centralbanking.accountservice.api.service.transaction.TransactionClearingServiceController;
import com.panut.centralbanking.accountservice.application.transactions.accountbalancechange.service.BalanceChangeService;
import com.panut.centralbanking.accountservice.application.transactions.clearing.modules.TransactionBalanceClearingSchedulerModuleV1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class TransactionBalanceClearingScheduler {
    private final TransactionBalanceClearingSchedulerModuleV1 v1Module;

    public TransactionBalanceClearingScheduler(TransactionBalanceClearingSchedulerModuleV1 v1Module) {
        this.v1Module = v1Module;
    }

    @Scheduled(fixedDelayString = "#{(60000 * " +
            "@transactionClearingProperty.getScheduleMinutes) + " +
            "@transactionClearingProperty.getScheduleMilliOffset}",
    initialDelay = 60000L)
    public Mono<Void> clearPending() {
       v1Module.clearPending().subscribe();

       return Mono.empty();
    }
}
