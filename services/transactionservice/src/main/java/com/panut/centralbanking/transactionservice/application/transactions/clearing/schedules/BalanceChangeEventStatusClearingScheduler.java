package com.panut.centralbanking.transactionservice.application.transactions.clearing.schedules;

import com.panut.centralbanking.transactionservice.application.transactions.clearing.schedules.modules.BalanceChangeEventStatusClearingModuleV1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class BalanceChangeEventStatusClearingScheduler {
    private final BalanceChangeEventStatusClearingModuleV1 v1Module;

    public BalanceChangeEventStatusClearingScheduler(BalanceChangeEventStatusClearingModuleV1 v1Module) {
        this.v1Module = v1Module;
    }


    @Scheduled(fixedDelayString = "#{(60000 * @transactionClearingProperty.getScheduleMinutes) + " +
           "@transactionClearingProperty.getScheduleMilliOffset}")
    public Mono<Void> clearPendings() {
        v1Module.clearPendings().subscribe();

        return Mono.empty();
    }
}
