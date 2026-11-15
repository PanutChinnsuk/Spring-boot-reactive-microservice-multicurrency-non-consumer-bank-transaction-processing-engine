package com.panut.centralbanking.transactionservice.integration.transaction.clearing;

import com.panut.centralbanking.appcore.integrationtest.SeededDatabase;
import com.panut.centralbanking.transactionservice.IntegrationDevTest;
import com.panut.centralbanking.transactionservice.api.service.account.AccountBalanceChangeServiceController;
import com.panut.centralbanking.transactionservice.api.service.account.request.GetProcessedBalanceEventIdsRequest;
import com.panut.centralbanking.transactionservice.application.transactions.clearing.schedules.modules.BalanceChangeEventStatusClearingModuleV1;
import com.panut.centralbanking.transactionservice.application.transactions.clearing.service.BalanceChangeEventClearingQueryService;
import com.panut.centralbanking.transactionservice.core.BalanceChangeEventStatus;
import com.panut.centralbanking.transactionservice.infrastructure.repository.BalanceChangeEventRepository;
import com.panut.centralbanking.transactionservice.testrepository.BalanceChangeEventTestRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BalanceChangeEventStatusClearingModuleTest implements IntegrationDevTest {
    @MockitoSpyBean
    private BalanceChangeEventClearingQueryService queryService;

    @MockitoSpyBean
    private AccountBalanceChangeServiceController serviceController;

    @Autowired
    private BalanceChangeEventStatusClearingModuleV1 v1Module;

    @Autowired
    private BalanceChangeEventTestRepository testRepository;

    @MockitoSpyBean
    private BalanceChangeEventRepository eventRepository;


    private String eventId1 = SeededDatabase.ID_PENDING_ACCOUNT_BALANCE_CHANGE_EVENT_1;
    private String eventId2 = SeededDatabase.ID_PENDING_ACCOUNT_BALANCE_CHANGE_EVENT_2;
    private Set<String> setIds = Set.of(eventId1, eventId2);
    private List<String> listIds = List.of(eventId1, eventId2);

    @AfterAll
    public void cleanUp() {
        testRepository.setAllStatusWithEventIds(setIds, BalanceChangeEventStatus.PENDING).block();
        testRepository.setAllBlacklistExpirty(setIds, LocalDateTime.now()
                .minusDays(1)).block();
    }



    @Test
    public void shouldCompleted() {

        doReturn(Flux.just(eventId1, eventId2))
                .when(eventRepository)
                        .findAllIdsForApplying(any(BalanceChangeEventStatus.class),
                                anyInt(),
                                anyString(),
                                any(LocalDateTime.class));


        doReturn(Mono.just(setIds))
                .when(serviceController)
                .getAppliableBalanceEvents(any(GetProcessedBalanceEventIdsRequest.class));

        v1Module.clearPendings().block();



        var event1 = testRepository.findById(eventId1).block();
        var event2 = testRepository.findById(eventId2).block();

        Assertions.assertEquals(BalanceChangeEventStatus.COMPLETED, event1.getStatus());
        Assertions.assertEquals(BalanceChangeEventStatus.COMPLETED, event2.getStatus());

    }
}
