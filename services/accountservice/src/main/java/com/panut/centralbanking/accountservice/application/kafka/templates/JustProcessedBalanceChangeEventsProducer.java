package com.panut.centralbanking.accountservice.application.kafka.templates;

import com.panut.centralbanking.accountservice.application.kafka.data.JustProcessedBalanceChangeEvents;
import com.panut.centralbanking.appcore.kafka.topics.SharedKafkaTopics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class JustProcessedBalanceChangeEventsProducer {
    private final KafkaTemplate<String, JustProcessedBalanceChangeEvents>
            template;

    public JustProcessedBalanceChangeEventsProducer(KafkaTemplate<String, JustProcessedBalanceChangeEvents>
                                                            justProcessedBalanceChangeEventTemplate) {
        this.template = justProcessedBalanceChangeEventTemplate;
    }

    private final String topic =
            SharedKafkaTopics.JUST_PROCESSED_BALANCE_CHANGE_EVENTS;

    public void unmandatorySend(final Set<String> justProcessedIds) {
        var data = new JustProcessedBalanceChangeEvents(justProcessedIds);

        try {
            template.send(topic, data);
            log.info("Send kafka topic, {}", topic);
        }catch (Exception e) {
            log.info("Failed to send kafka topic, {}, {}", topic,
                    e.getLocalizedMessage());
        }



    }
}
