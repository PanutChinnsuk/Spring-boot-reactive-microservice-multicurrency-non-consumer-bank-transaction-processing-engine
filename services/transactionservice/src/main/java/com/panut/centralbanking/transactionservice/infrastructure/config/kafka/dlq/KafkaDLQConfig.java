package com.panut.centralbanking.transactionservice.infrastructure.config.kafka.dlq;

import com.panut.centralbanking.transactionservice.application.kafkalistener.data.JustProcessedBalanceChangeEvents;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.BackOffHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaDLQConfig {


    @Bean
    public DefaultErrorHandler justProcessedBalanceChangeEventDLQ(
            KafkaTemplate<String, JustProcessedBalanceChangeEvents>
                    justProcessedBalanceChangeEventTemplate
    ) {
        return defaultErrorHandler(justProcessedBalanceChangeEventTemplate);
    }


    private DefaultErrorHandler defaultErrorHandler(KafkaTemplate<?, ?> template) {
        DefaultErrorHandler handler = new DefaultErrorHandler(
                new DeadLetterPublishingRecoverer(template),
                new FixedBackOff(2500L, 4));

        handler.addNotRetryableExceptions(SerializationException.class);
        handler.addNotRetryableExceptions(DeserializationException.class);

        return handler;
    }
}
