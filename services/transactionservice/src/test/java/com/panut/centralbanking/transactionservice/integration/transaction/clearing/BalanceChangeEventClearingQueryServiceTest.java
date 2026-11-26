package com.panut.centralbanking.transactionservice.integration.transaction.clearing;

import com.panut.centralbanking.appcore.integrationtest.SeededDatabase;
import com.panut.centralbanking.appcore.integrationtest.SeededDatabaseIdFlag;
import com.panut.centralbanking.transactionservice.IntegrationDevTest;
import com.panut.centralbanking.transactionservice.api.service.account.data.BalanceChangeData;
import com.panut.centralbanking.transactionservice.application.property.TransactionClearingProperty;
import com.panut.centralbanking.transactionservice.application.transactions.clearing.service.BalanceChangeEventClearingQueryService;
import com.panut.centralbanking.transactionservice.core.BalanceChangeEventStatus;
import com.panut.centralbanking.transactionservice.core.TransactionStatus;
import com.panut.centralbanking.transactionservice.infrastructure.model.events.BalanceChangeEvent;
import com.panut.centralbanking.transactionservice.infrastructure.repository.BalanceChangeEventRepository;
import com.panut.centralbanking.transactionservice.integration.transaction.cleanrepo.RepositoryCleanUp;
import com.panut.centralbanking.transactionservice.integration.transaction.clearing.manager.TestEventManager;
import com.panut.centralbanking.transactionservice.integration.transaction.isolation.orchest.BalanceChangeEventClearingQueryISO;
import com.panut.centralbanking.transactionservice.testrepository.BalanceChangeEventTestRepository;
import com.panut.centralbanking.transactionservice.testrepository.TransactionOverdraftContentTestRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.LocalDateTime;
import java.util.*;

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

    @Autowired
    private BalanceChangeEventClearingQueryISO iso;

    @Autowired
    private RepositoryCleanUp cleanRepo;


    private String transactionId1;
    private String transactionId2;
    private String overdraft_transaction_id;


    @BeforeAll
    public void setUp() {
        transactionId1 = iso.commonCase().getId();
        transactionId2 = iso.commonCase().getId();
        overdraft_transaction_id = iso.overdraftCase().getId();
    }

    @AfterAll
    public void cleanUp() {
        cleanRepo.hardCleanUp(List.of(
                transactionId1,
                transactionId2,
                overdraft_transaction_id
        ));
    }

    @Test
    public void shouldSort() {
        var chain = query.getSortedEachTransaction(TransactionStatus.PENDING).block();
        Map<String, Sinks.Many<BalanceChangeEvent>> poop = chain.getPerTransactionSink();




        for (Map.Entry<String, Sinks.Many<BalanceChangeEvent>> entry : poop.entrySet()) {
            List<BalanceChangeEvent> events = entry.getValue().asFlux().collectList().block();


            Assertions.assertTrue(!events.isEmpty());



            events.forEach(event -> {
                Assertions.assertEquals(entry.getKey(),
                        event.getTransactionId());
            });

        }
    }

    @Test
    public void shouldGetAndMappedDTOAndIncrement() {

        var eventStatus = BalanceChangeEventStatus.PENDING;

        LocalDateTime incremented = property.getNewBlacklistExpiry();
        List<BalanceChangeData> datas = query.getBulk(eventStatus, 100,
                incremented, SeededDatabaseIdFlag.NONE).collectList().block();

        Assertions.assertTrue(!datas.isEmpty());

        datas.forEach(event -> {
            Assertions.assertNotNull(event.getEventId(), "event id");
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



        var postValue = post.getApplyingExpiresAt();
        Assertions.assertTrue(LocalDateTime.now()
                .isBefore(postValue));


    }
}
