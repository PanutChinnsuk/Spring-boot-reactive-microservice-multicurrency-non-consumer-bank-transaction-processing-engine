package com.panut.centralbanking.transactionservice.integration.transaction.clearing;

import com.panut.centralbanking.transactionservice.IntegrationDevTest;
import com.panut.centralbanking.transactionservice.application.transactions.clearing.query.TransactionClearingQuery;
import com.panut.centralbanking.transactionservice.core.TransactionStatus;
import com.panut.centralbanking.transactionservice.infrastructure.model.events.BalanceChangeEvent;
import com.panut.centralbanking.transactionservice.infrastructure.repository.BalanceChangeEventRepository;
import com.panut.centralbanking.transactionservice.testrepository.BalanceChangeEventTestRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.Map;

public class TransactionClearingQueryTest implements IntegrationDevTest {
    @Autowired
    private TransactionClearingQuery query;

    @Autowired
    private BalanceChangeEventTestRepository repository;


    @Test
    public void shouldMap() {
        var chain = query.getClearingData(TransactionStatus.PENDING).block();
        Map<String, Sinks.Many<BalanceChangeEvent>> poop = chain.getPerTransactionSink();




        for (Map.Entry<String, Sinks.Many<BalanceChangeEvent>> entry : poop.entrySet()) {
            List<BalanceChangeEvent> events = entry.getValue().asFlux().collectList().block();

            List<BalanceChangeEvent> eventSizes = repository
                    .findByTransactionId(entry.getKey()).collectList().block();




            Assertions.assertTrue(!events.isEmpty());
            Assertions.assertEquals(eventSizes.size(), events.size());


            events.forEach(event -> {
                Assertions.assertEquals(entry.getKey(),
                        event.getTransactionId());
            });

        }
    }
}
