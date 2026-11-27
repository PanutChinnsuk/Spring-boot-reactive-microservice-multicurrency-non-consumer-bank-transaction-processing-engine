package com.panut.centralbanking.transactionservice.application.kafkalistener;

import com.panut.centralbanking.appcore.exceptions.SystemMisused;
import com.panut.centralbanking.appcore.kafka.topics.SharedKafkaTopics;
import com.panut.centralbanking.transactionservice.application.transactions.clearing.schedules.modules.BalanceChangeEventStatusClearingModuleV1;
import com.panut.centralbanking.transactionservice.application.kafkalistener.data.JustProcessedBalanceChangeEvents;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.backoff.FixedBackOff;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class ProcessedBalanceChangeEventListenerHandler {
    private final BalanceChangeEventStatusClearingModuleV1 v1Module;

    public ProcessedBalanceChangeEventListenerHandler(BalanceChangeEventStatusClearingModuleV1 v1Module) {
        this.v1Module = v1Module;
    }


    @RetryableTopic(
            attempts = "3",
            dltStrategy = DltStrategy.FAIL_ON_ERROR
    )
    @KafkaListener(topics = SharedKafkaTopics.JUST_PROCESSED_BALANCE_CHANGE_EVENTS,
    containerFactory = "processedBalanceChangeEventListener")
    public Mono<Void> clearProcessed(JustProcessedBalanceChangeEvents event) {
        v1Module.bulkUpdateWithWorker(event.getJustProcessedIds()).subscribe();

        log.info("clearedProcessed kafka event, {}", event.getJustProcessedIds());

        return Mono.empty();
    }
}
