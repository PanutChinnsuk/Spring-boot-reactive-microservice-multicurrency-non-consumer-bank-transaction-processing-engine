package com.panut.centralbanking.transactionservice.infrastructure.config.kafka.topic;

import com.panut.centralbanking.appcore.kafka.topics.SharedKafkaTopics;
import com.panut.centralbanking.transactionservice.application.kafkalistener.data.JustProcessedBalanceChangeEvents;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class BalanceChangeEventTopics {

    @Bean
    public NewTopic processedBalanceChangeEventTopic() {
        return TopicBuilder.name(SharedKafkaTopics.JUST_PROCESSED_BALANCE_CHANGE_EVENTS)
                .partitions(3)
                .replicas(3)
                .build();
    }


    @Bean
    public ConsumerFactory<String, JustProcessedBalanceChangeEvents>
    processedBalanceChangeEventConsumerFactory(KafkaProperties properties) {
        Map<String, Object> config = new HashMap<>();


        var valueDeserializer = new JacksonJsonDeserializer<>(
                JustProcessedBalanceChangeEvents.class
        );
        valueDeserializer.trustedPackages("*");
        valueDeserializer.ignoreTypeHeaders();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBootstrapServers());
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.GROUP_ID_CONFIG,
                properties.getConsumer().getGroupId());




        return new DefaultKafkaConsumerFactory<>(config,
                null, valueDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, JustProcessedBalanceChangeEvents>
    processedBalanceChangeEventListener(ConsumerFactory<String, JustProcessedBalanceChangeEvents>
                                                processedBalanceChangeEventConsumerFactory) {

        var listener = new ConcurrentKafkaListenerContainerFactory<String,
                JustProcessedBalanceChangeEvents>();
        listener.setConsumerFactory(processedBalanceChangeEventConsumerFactory);

        return listener;
    }
}
