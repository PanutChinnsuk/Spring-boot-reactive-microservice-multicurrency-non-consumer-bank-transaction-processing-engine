package com.panut.centralbanking.transactionservice.integration.transaction.manage;

import com.panut.centralbanking.appR2dbccore.data.MapListSorter;
import com.panut.centralbanking.appcore.integrationtest.SeededDatabase;
import com.panut.centralbanking.transactionservice.IntegrationDevTest;
import com.panut.centralbanking.transactionservice.api.service.account.AccountBalanceChangeServiceController;
import com.panut.centralbanking.transactionservice.api.service.account.request.GetProcessedBalanceEventIdsRequest;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.cancellation.TransactionCancellationPipeline;
import com.panut.centralbanking.transactionservice.core.BalanceChangeEventStatus;
import com.panut.centralbanking.transactionservice.core.TransactionStatus;
import com.panut.centralbanking.transactionservice.infrastructure.model.events.BalanceChangeEvent;
import com.panut.centralbanking.transactionservice.integration.transaction.cleanrepo.TransactionTestRepositoryCleanUp;
import com.panut.centralbanking.transactionservice.integration.transaction.isolation.TransactionCancellationISO;
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

record CancelledLedger(BigDecimal totalBalanceDeduct,
                       BigDecimal totalBalanceAdd,
                       BigDecimal totalReserveDeduct,
                       BigDecimal totalReserveAdd,
                       BigDecimal totalOverdraftAdd,
                       BigDecimal totalOverdraftDeduct) {};

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

    @Autowired
    private TransactionCancellationISO iso;

    @Autowired
    private TransactionTestRepositoryCleanUp cleanRepo;


    private String noProc;
    private String proc = SeededDatabase
            .ID_TRANSACTION_FOR_CANCEL_PROC_EVENTS;
    private String procWithOverdraft = SeededDatabase
            .ID_TRANSACTION_FOR_CANCEL_PROC_EVENTS_WITH_OVERDRAFT_CONTENTS;
    private String everything = SeededDatabase
            .ID_TRANSACTION_FOR_CANCEL_HAS_EVERYTHING;

    private List<String> transactionIds = new ArrayList<>();

    private List<BalanceChangeEvent> getNewCreatedEvents(String transactionId,
                                                         List<String> excludedEventIds) {
        return eventTestRepository
                .getNewCreated(transactionId,
                        excludedEventIds)
                .collectList().block();
    }

    private List<String> getEventIds(String transaction) {
        return eventTestRepository.getCreatedIds(noProc).collectList().block();
    }

    private Map<String, BalanceChangeEventStatus> rollBack = new HashMap<>();

    @BeforeAll
    public void setUp() {
        noProc = iso.caseNoProc().getId();

        transactionIds.add(noProc);

       /* eventTestRepository
                .findByTransactionIdIn(transactionIds)
                .collectList()
                .block()
                .forEach(e -> {
                    rollBack.put(e.getId(), e.getStatus());
                });*/
    }

    @AfterAll
    public void cleanUp() {
        cleanRepo.cleanUpEvents(transactionIds);
        cleanRepo.cleanUp(transactionIds);

    }


    @Test
    public void shouldAllCancelled() {
        doReturn(Mono.just(Collections.emptySet()))
                .when(serviceController)
                .getAppliableBalanceEvents(any(GetProcessedBalanceEventIdsRequest.class));

        var oldEventIds = getEventIds(noProc);

        var pre = eventTestRepository
                .findByTransactionId(noProc).collectList()
                .block();

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

        Assertions.assertTrue(getNewCreatedEvents(noProc, oldEventIds).isEmpty(), "no new event create");
    }


   // @Test
    public void shouldCancelledWorstCase() {
        var pre = eventTestRepository
                .findByTransactionId(everything).collectList()
                .block();

        var processedIds = getIdsortByStatus(pre, BalanceChangeEventStatus.COMPLETED);
        var pendingIds = getIdsortByStatus(pre, BalanceChangeEventStatus.PENDING);

        Assertions.assertTrue(processedIds.size() > 0, "proc ids size");
        Assertions.assertTrue(pendingIds.size() > 0, "pending ids size");

        var previousIds = new ArrayList<>(processedIds);
        previousIds.addAll(pendingIds);

        doReturn(Mono.just(processedIds))
                .when(serviceController)
                .getAppliableBalanceEvents(any(GetProcessedBalanceEventIdsRequest.class));

        cancellationPipeline.cancel(everything).block();

        var post = eventTestRepository
                .findByIdIn(previousIds).collectList()
                .block();

        post.stream().filter(a -> a.getStatus().isPendingState())
                .forEach(e -> {
            Assertions.assertEquals(BalanceChangeEventStatus.CANCELLED,
                    e.getStatus());
        });


        var newEvents = eventTestRepository
                .getNewCreated(everything, previousIds).collectList().block();

        newEvents.forEach(e -> {
            Assertions.assertEquals(e.getTransactionId(), everything);
        });


        Assertions.assertTrue(!newEvents.isEmpty(), "new events empty");





        var preMap = getPerAccountMap(pre);
        var postMap = getPerAccountMap(newEvents);

        var completedStatus = BalanceChangeEventStatus.COMPLETED;
        var pendingStatus = BalanceChangeEventStatus.PENDING;

        preMap.forEach((accountId, events) -> {



            var rollBacks = postMap.get(accountId);
            Assertions.assertNotNull(rollBacks, "null at " + accountId);
            Assertions.assertTrue(!rollBacks.isEmpty(), "empty at " + accountId);


            var preAccumelated = getAccumelatedLedger(events, completedStatus);
            var newAccumelated = getAccumelatedLedger(rollBacks, pendingStatus);


            var balAdd = preAccumelated.totalBalanceAdd();
            var newBalDed = newAccumelated.totalBalanceDeduct();
            if (balAdd.signum() > 0)
                Assertions.assertEquals(balAdd, newBalDed,
                        flagString("ledger balance add", accountId));

            var balDed = preAccumelated.totalBalanceDeduct();
            var newBalAdd = newAccumelated.totalBalanceAdd();
            if (balDed.signum() > 0)
                Assertions.assertEquals(balDed, newBalAdd,
                        flagString("ledger balance deduct", accountId));

            var ovdAdd = preAccumelated.totalOverdraftAdd();
            var newOvdDed = newAccumelated.totalOverdraftDeduct();
            if (ovdAdd.signum() > 0)
                Assertions.assertEquals(ovdAdd, newOvdDed,
                        flagString("ledger overdraft add", accountId));

            var ovdDed = preAccumelated.totalOverdraftDeduct();
            var newOvdAdd = newAccumelated.totalOverdraftAdd();
            if (ovdDed.signum() > 0)
                Assertions.assertEquals(ovdDed, newOvdAdd,
                        flagString("ledger overdraft deduct", accountId));

            var resAdd = preAccumelated.totalReserveAdd();
            var resDed = preAccumelated.totalReserveDeduct();
            var newResAdd = newAccumelated.totalReserveAdd();
            var newResDed = newAccumelated.totalReserveDeduct();

            Assertions.assertTrue(newResAdd.signum() <= 0,
                    "impossible system rollback");

            if (resAdd.signum() > 0 && resDed.signum() > 0) {
                BigDecimal instantRefund = resAdd;

                if (resAdd.compareTo(resDed) >= 0) {
                    instantRefund = resAdd.subtract(resDed);

                    if (instantRefund.signum() <= 0)
                        instantRefund = resDed;
                }

                var mergedRefund = newBalAdd.subtract(newBalAdd.subtract(instantRefund));
                Assertions.assertEquals(mergedRefund,
                        instantRefund,
                        flagString("refund", accountId));
            }


        });



    }

    private String flagString(String any, String key) {
        return any + " " + key;
    }

    private Map<String,List<BalanceChangeEvent>> getPerAccountMap(List<BalanceChangeEvent> list) {
        Map<String, List<BalanceChangeEvent>> map = new HashMap<>();

        list.forEach(e -> {
            MapListSorter.sortMapList(map,
                    e.getAccountId(), e);
        });


        return map;
    }

    private CancelledLedger getAccumelatedLedger(List<BalanceChangeEvent> events,
                                                 BalanceChangeEventStatus status) {
        var reserveDeducted = BigDecimal.ZERO;
        var reserveAdded = BigDecimal.ZERO;
        var balanceDeducted = BigDecimal.ZERO;
        var balanceAdded = BigDecimal.ZERO;
        var overdraftDeducted = BigDecimal.ZERO;
        var overdraftAdded = BigDecimal.ZERO;

        for (int i = 0; i < events.size(); i++) {
            var event = events.get(i);

            if (!event.getStatus().equals(status))
                continue;

            var isAdd = event.getBalanceImpact().ADD();
            var isDeduct = event.getBalanceImpact().DEDUCT();
            var type = event.getBalanceChangeType();
            var amount = event.getAmount();



            if (type.BALANCE()) {
                if (isAdd)
                    balanceAdded = balanceAdded.add(amount);
                if (isDeduct)
                    balanceDeducted = balanceDeducted.add(amount);
            }

            if (type.RESERVE()) {
                if (isAdd)
                    reserveAdded = reserveAdded.add(amount);
                if (isDeduct)
                    reserveDeducted = reserveDeducted.add(amount);
            }

            if (type.requiredTransactionOverdraftContent()) {
                if (isAdd)
                    overdraftAdded = overdraftAdded.add(amount);
                if (isDeduct)
                    overdraftDeducted = overdraftDeducted.add(amount);
            }
        }

        return new CancelledLedger(balanceDeducted, balanceAdded,
                reserveDeducted,
                reserveAdded,
                overdraftAdded,
                overdraftDeducted);
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
