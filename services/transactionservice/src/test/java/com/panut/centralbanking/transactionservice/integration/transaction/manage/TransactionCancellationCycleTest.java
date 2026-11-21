package com.panut.centralbanking.transactionservice.integration.transaction.manage;

import com.panut.centralbanking.appcore.integrationtest.SeededDatabase;
import com.panut.centralbanking.appcore.integrationtest.SeededDatabaseIdFlag;
import com.panut.centralbanking.transactionservice.IntegrationDevTest;
import com.panut.centralbanking.transactionservice.api.service.account.AccountBalanceChangeServiceController;
import com.panut.centralbanking.transactionservice.api.service.account.data.related.BalanceChangeType;
import com.panut.centralbanking.transactionservice.api.service.account.data.related.BalanceImpact;
import com.panut.centralbanking.transactionservice.api.service.account.request.GetProcessedBalanceEventIdsRequest;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.cancellation.TransactionCancellationPipeline;
import com.panut.centralbanking.transactionservice.core.BalanceChangeEventStatus;
import com.panut.centralbanking.transactionservice.core.TransactionStatus;
import com.panut.centralbanking.transactionservice.infrastructure.model.events.BalanceChangeEvent;
import com.panut.centralbanking.transactionservice.testrepository.BalanceChangeEventTestRepository;
import com.panut.centralbanking.transactionservice.testrepository.TransactionTestRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionCancellationCycleTest implements IntegrationDevTest {
    @Autowired
    private TransactionCancellationPipeline cancellationPipeline;

    @Autowired
    private TransactionTestRepository transactionTestRepository;

    @Autowired
    private BalanceChangeEventTestRepository eventTestRepository;

    @MockitoSpyBean
    private AccountBalanceChangeServiceController serviceController;


    private String noProc = SeededDatabase.ID_TRANSACTION_FOR_CANCEL_NO_PROC_EVENTS;
    private String proc = SeededDatabase
            .ID_TRANSACTION_FOR_CANCEL_PROC_EVENTS;
    private String procWithOverdraft = SeededDatabase
            .ID_TRANSACTION_FOR_CANCEL_PROC_EVENTS_WITH_OVERDRAFT_CONTENTS;
    private List<String> transactionIds = List.of(
            noProc,
            proc,
            procWithOverdraft
    );

    private List<String> getNewCreated(String transactionId) {
        return eventTestRepository
                .getNewCreated(List.of(transactionId),
                        SeededDatabaseIdFlag.TEST)
                .collectList().block();
    }

    private Map<String, BalanceChangeEventStatus> rollBack = new HashMap<>();

    @BeforeAll
    public void setUp() {
        eventTestRepository
                .findByTransactionIdIn(transactionIds)
                .collectList()
                .block()
                .forEach(e -> {
                    rollBack.put(e.getId(), e.getStatus());
                });
    }

    @AfterAll
    public void cleanUp() {
        rollBack.forEach((k,v) -> {
            eventTestRepository.rollbackStatus(k, v).block();
        });

        transactionTestRepository.setStatus(TransactionStatus.PENDING_POSTED,
                transactionIds).block();

        List<String> newEventIds = eventTestRepository
                .getNewCreated(transactionIds,
                        SeededDatabaseIdFlag.TEST)
                .collectList().block();
        eventTestRepository.deleteByIdIn(newEventIds).block();
    }


    @Test
    public void shouldAllCancelled() {
        doReturn(Mono.just(Collections.emptySet()))
                .when(serviceController)
                .getAppliableBalanceEvents(any(GetProcessedBalanceEventIdsRequest.class));


        var pre = eventTestRepository
                .findByTransactionId(noProc).collectList()
                .block();

        BigDecimal totalAdd = getTotalLedger(pre, BalanceImpact.ADD);
        BigDecimal totalDeduct = getTotalLedger(pre, BalanceImpact.DEDUCT);

        cancellationPipeline
                .cancel(noProc).block();

        var transaction = transactionTestRepository
                .findById(noProc).block();
        var post = eventTestRepository
                .findByTransactionId(noProc).collectList()
                .block();

        Assertions.assertTrue(!post.isEmpty(), "seeded not empty");

        post.forEach(e -> {
            Assertions.assertEquals(BalanceChangeEventStatus.CANCELLED,
                    e.getStatus(), "each event status");
        });
        Assertions.assertEquals(TransactionStatus.PENDING_CANCELLED,
                transaction.getTransactionStatus(), "tx status");

        Assertions.assertTrue(getNewCreated(noProc).isEmpty(), "no new event create");
    }


    //@Test
    public void shouldAllCancelledAndReverse() {
        var pre = eventTestRepository
                .findByTransactionId(proc).collectList()
                .block();

        var processedIds = getIdsortByStatus(pre, BalanceChangeEventStatus.COMPLETED);
        var pendingIds = getIdsortByStatus(pre, BalanceChangeEventStatus.PENDING);

        doReturn(Mono.just(processedIds))
                .when(serviceController)
                .getAppliableBalanceEvents(any(GetProcessedBalanceEventIdsRequest.class));

        cancellationPipeline.cancel(proc).block();


        var cancelledPendings = getByIds(pendingIds);
        var reversedProcessed = getByIds(processedIds);



    }

    private BigDecimal getTotalLedger(List<BalanceChangeEvent> events,
                                      BalanceChangeType balanceChangeType,
                                      BalanceImpact balanceImpact) {
        return events
                .stream()
                .filter(a -> a.getBalanceImpact().equals(balanceImpact)
                && a.getBalanceChangeType().equals(balanceChangeType)

                ).map(a -> a.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getTotalLedger(List<BalanceChangeEvent> events,
                                      BalanceImpact balanceImpact) {
        return events
                .stream()
                .filter(a -> a.getBalanceImpact().equals(balanceImpact)
                ).map(a -> a.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Set<String> getIdsortByStatus(List<BalanceChangeEvent> events,
                                     BalanceChangeEventStatus status) {
        return events.stream().filter(a ->
                a.getStatus().equals(status)
        ).map(e -> e.getId()).collect(Collectors.toSet());
    }

    private List<BalanceChangeEvent> getByIds(Collection<String> eventIds) {
        return eventTestRepository
                .findByIdIn(eventIds)
                .collectList()
                .block();
    }
}
