package com.panut.centralbanking.transactionservice.integration.transaction.clearing;

import com.panut.centralbanking.appcore.integrationtest.SeededDatabase;
import com.panut.centralbanking.appcore.integrationtest.SeededDatabaseIdFlag;
import com.panut.centralbanking.transactionservice.IntegrationDevTest;
import com.panut.centralbanking.transactionservice.api.service.account.data.BalanceChangeData;
import com.panut.centralbanking.transactionservice.api.service.account.data.related.BalanceChangeType;
import com.panut.centralbanking.transactionservice.api.service.account.data.related.BalanceImpact;
import com.panut.centralbanking.transactionservice.application.events.balancechange.BalanceChangeEventRepositoryManager;
import com.panut.centralbanking.transactionservice.application.property.TransactionClearingProperty;
import com.panut.centralbanking.transactionservice.application.transactions.clearing.service.BalanceChangeEventClearingQueryService;
import com.panut.centralbanking.transactionservice.core.BalanceChangeEventStatus;
import com.panut.centralbanking.transactionservice.core.TransactionStatus;
import com.panut.centralbanking.transactionservice.infrastructure.model.events.BalanceChangeEvent;
import com.panut.centralbanking.transactionservice.infrastructure.repository.BalanceChangeEventRepository;
import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionOverdraftContentRepository;
import com.panut.centralbanking.transactionservice.integration.transaction.clearing.manager.TestEventManager;
import com.panut.centralbanking.transactionservice.testrepository.BalanceChangeEventTestRepository;
import com.panut.centralbanking.transactionservice.testrepository.TransactionOverdraftContentTestRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BalanceChangeEventClearingQueryServiceTest implements IntegrationDevTest {
    @Autowired
    private BalanceChangeEventClearingQueryService query;


    @Autowired
    private BalanceChangeEventTestRepository testRepository;

    @Autowired
    private TransactionClearingProperty property;

    @Autowired
    private TestEventManager manager;

    @MockitoSpyBean
    private BalanceChangeEventRepository eventRepository;

    @Autowired
    private TransactionOverdraftContentTestRepository contentTestRepository;


    private String transactionId1 = SeededDatabase.ID_PENDING_TRANSACTION_1;
    private String transactionId2 = SeededDatabase.ID_PENDING_TRANSACTION_2;
    private String overdraft_transaction_id = SeededDatabase.ID_EMPTY_OVERDRAFT_PAYMENT_TYPE_TRANSACTION_1;
    private List<String> eventIds = new ArrayList<>();
    private List<String> toDelete = new ArrayList<>();

    @AfterAll
    public void cleanUp() {
        if (!eventIds.isEmpty())
            testRepository.setAllBlacklistExpirty(eventIds,
                LocalDateTime.now().minusDays(1)).block();

        manager.clear();
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

        var eventStatus = BalanceChangeEventStatus.PENDING;



        testRepository.setAllStatusWithTransactionIds(Set.of(transactionId1, transactionId2),
                eventStatus).block();


        LocalDateTime incremented = property.getNewBlacklistExpiry();
        List<BalanceChangeData> datas = query.getBulk(eventStatus, 100,
                incremented, SeededDatabaseIdFlag.NONE).collectList().block();

        Assertions.assertTrue(!datas.isEmpty());

        datas.forEach(event -> {
            Assertions.assertNotNull(event.getEventId(), "event id");

            eventIds.add(event.getEventId());
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

    }

    @Test
    public void shouldReturnWithOverdraftContext() {
        List<String> eventIds = testRepository.findByTransactionId(overdraft_transaction_id)
                .collectList().block()
                .stream().map(a -> a.getId()).toList();

        int size = contentTestRepository.findByTransactionId(overdraft_transaction_id)
                        .collectList().block().size();

        eventIds.forEach(a -> {
            Assertions.assertNotNull(a);
            this.eventIds.add(a);
        });

        doReturn(Flux.fromIterable(eventIds))
                .when(eventRepository).findAllIdsForClearing(
                        any(BalanceChangeEventStatus.class),
                        anyInt(),
                        anyString(),
                        any(LocalDateTime.class),
                        anyCollection()
                );

        List<BalanceChangeData> datas = query.getBulk(BalanceChangeEventStatus.PENDING,
                100,
                LocalDateTime.now(), SeededDatabaseIdFlag.NONE).collectList().block();

        datas.forEach(data -> {
            Assertions.assertNotNull(data.getOverdraftChangeContext());
            Assertions.assertEquals(data.getOverdraftChangeContext().getAccountOverdraftIds()
                    .size(), size);
        });

    }

    @Test
    public void shouldIncrementApplyingExpiry() {
        String thisId = manager.create(transactionId1);


        var ids = query.apply(BalanceChangeEventStatus.PENDING, 100,
                property.getNewApplyingBlacklistExpiry(),
                SeededDatabaseIdFlag.NONE).block();



        var post = testRepository.findById(thisId).block();


        eventIds.addAll(ids);



        var postValue = post.getApplyingExpiresAt();
        Assertions.assertTrue(LocalDateTime.now()
                .isBefore(postValue));


    }
}
