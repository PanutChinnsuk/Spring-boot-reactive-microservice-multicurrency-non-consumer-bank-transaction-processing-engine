package com.panut.centralbanking.transactionservice.integration.transaction.clearing;

import com.panut.centralbanking.appcore.integrationtest.SeededDatabase;
import com.panut.centralbanking.appcore.integrationtest.SeededDatabaseIdFlag;
import com.panut.centralbanking.transactionservice.IntegrationDevTest;
import com.panut.centralbanking.transactionservice.api.service.account.data.BalanceChangeData;
import com.panut.centralbanking.transactionservice.application.property.TransactionClearingProperty;
import com.panut.centralbanking.transactionservice.application.transactions.clearing.query.TransactionClearingQuery;
import com.panut.centralbanking.transactionservice.core.BalanceChangeEventStatus;
import com.panut.centralbanking.transactionservice.core.TransactionStatus;
import com.panut.centralbanking.transactionservice.infrastructure.model.events.BalanceChangeEvent;
import com.panut.centralbanking.transactionservice.infrastructure.repository.BalanceChangeEventRepository;
import com.panut.centralbanking.transactionservice.testrepository.BalanceChangeEventTestRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Sinks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionClearingQueryTest implements IntegrationDevTest {
    @Autowired
    private TransactionClearingQuery query;

    @Autowired
    private BalanceChangeEventRepository repository;

    @Autowired
    private BalanceChangeEventTestRepository testRepository;

    @Autowired
    private TransactionClearingProperty property;

    private List<String> ids = new ArrayList<>();

    @AfterAll
    public void cleanUp() {
        if (ids.isEmpty())
            return;

        testRepository.setAllBlacklistExpirty(ids,
                LocalDateTime.now().minusDays(1)).block();
    }

    @Test
    public void shouldSort() {
        var chain = query.getSortedEachTransaction(TransactionStatus.PENDING).block();
        Map<String, Sinks.Many<BalanceChangeEvent>> poop = chain.getPerTransactionSink();




        for (Map.Entry<String, Sinks.Many<BalanceChangeEvent>> entry : poop.entrySet()) {
            List<BalanceChangeEvent> events = entry.getValue().asFlux().collectList().block();

            List<BalanceChangeEvent> eventSizes = testRepository
                    .findByTransactionId(entry.getKey()).collectList().block();




            Assertions.assertTrue(!events.isEmpty());
            Assertions.assertEquals(eventSizes.size(), events.size());


            events.forEach(event -> {
                Assertions.assertEquals(entry.getKey(),
                        event.getTransactionId());
            });

        }
    }

    @Test
    public void shouldGetAndMappedDTOAndIncrement() {
        String id1 = SeededDatabase.ID_PENDING_TRANSACTION_1;
        String id2 = SeededDatabase.ID_PENDING_TRANSACTION_2;
        var eventStatus = BalanceChangeEventStatus.PENDING;



        testRepository.setAllStatus(Set.of(id1, id2),
                eventStatus).block();


        LocalDateTime incremented = property.getNewBlacklistExpiry();
        List<BalanceChangeData> datas = query.getBulk(eventStatus, 100,
                incremented, SeededDatabaseIdFlag.NONE).collectList().block();

        datas.forEach(event -> {
            Assertions.assertNotNull(event.getEventId(), "event id");
            ids.add(event.getEventId());
        });


        datas.forEach(event -> {
            Assertions.assertNotNull(event.getAmount(), "amount");
            Assertions.assertNotNull(event.getBalanceImpact(), "balance impact");
            Assertions.assertNotNull(event.getBalanceChangeType(), "balance change type");
            Assertions.assertNotNull(event.getCreatedAt(), "created at");
            Assertions.assertNotNull(event.getTransactionId(), "transaction id");

            Assertions.assertNotNull(event.getBlacklistExpiry());
            Assertions.assertTrue(event.getBlacklistExpiry()
                    .isAfter(LocalDateTime.now()));
        });

        int t1Size = testRepository.findByTransactionId(id1).collectList().block().size();
        int t2Size = testRepository.findByTransactionId(id2).collectList().block().size();

        Assertions.assertEquals(datas.size(), t1Size + t2Size);
    }
}
