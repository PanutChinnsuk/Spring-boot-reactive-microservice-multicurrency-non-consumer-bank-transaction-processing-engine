package com.panut.centralbanking.transactionservice.application.kafkalistener;

import com.panut.centralbanking.appcore.exceptions.SystemMisused;
import com.panut.centralbanking.appcore.kafka.topics.SharedKafkaTopics;
import com.panut.centralbanking.transactionservice.application.transactions.clearing.schedules.modules.BalanceChangeEventStatusClearingModuleV1;
import com.panut.centralbanking.transactionservice.application.kafkalistener.data.JustProcessedBalanceChangeEvents;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ProcessedBalanceChangeEventListenerHandler {
    private final BalanceChangeEventStatusClearingModuleV1 v1Module;

    public ProcessedBalanceChangeEventListenerHandler(BalanceChangeEventStatusClearingModuleV1 v1Module) {
        this.v1Module = v1Module;
    }


    @KafkaListener(topics = SharedKafkaTopics.JUST_PROCESSED_BALANCE_CHANGE_EVENTS,
    containerFactory = "processedBalanceChangeEventListener")
    public Mono<Void> clearProcessed(JustProcessedBalanceChangeEvents event) {
        v1Module.bulkUpdateWithWorker(event.getJustProcessedIds()).subscribe();
        
        log.info("clearedProcessed kafka event, {}", event.getJustProcessedIds());

        return Mono.empty();
    }
}
