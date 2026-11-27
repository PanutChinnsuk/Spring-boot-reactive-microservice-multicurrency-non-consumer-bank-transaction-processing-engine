package com.panut.centralbanking.accountservice.infrastructure.config.kafka.producer;

import com.panut.centralbanking.accountservice.application.kafka.data.JustProcessedBalanceChangeEvents;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class BalanceChangeEventsTopic {


    @Bean
    public ProducerFactory<String, JustProcessedBalanceChangeEvents>
    justProcessedBalanceChangeEventsProducerFactory(KafkaProperties prop) {
        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                prop.getBootstrapServers());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JacksonJsonSerializer.class);
        config.put(ProducerConfig.RETRIES_CONFIG, 1);
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);



        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, JustProcessedBalanceChangeEvents>
            justProcessedBalanceChangeEventTemplate(ProducerFactory<String, JustProcessedBalanceChangeEvents>
                                                            justProcessedBalanceChangeEventsProducerFactory) {


        var template = new KafkaTemplate<>(justProcessedBalanceChangeEventsProducerFactory);

        return template;
    }



}
