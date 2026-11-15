package com.panut.centralbanking.transactionservice.integration.transaction.clearing;

import com.panut.centralbanking.appcore.integrationtest.SeededDatabase;
import com.panut.centralbanking.transactionservice.IntegrationDevTest;
import com.panut.centralbanking.transactionservice.api.service.account.AccountBalanceChangeServiceController;
import com.panut.centralbanking.transactionservice.api.service.account.request.GetProcessedBalanceEventIdsRequest;
import com.panut.centralbanking.transactionservice.application.events.balancechange.BalanceChangeEventRepositoryManager;
import com.panut.centralbanking.transactionservice.application.transactions.clearing.schedules.modules.BalanceChangeEventStatusClearingModuleV1;
import com.panut.centralbanking.transactionservice.application.transactions.clearing.service.BalanceChangeEventClearingQueryService;
import com.panut.centralbanking.transactionservice.core.BalanceChangeEventStatus;
import com.panut.centralbanking.transactionservice.infrastructure.repository.BalanceChangeEventRepository;
import com.panut.centralbanking.transactionservice.integration.transaction.clearing.manager.TestEventManager;
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

    @Autowired
    private TestEventManager manager;


    @AfterAll
    public void cleanUp() {
        manager.clear();
    }


    @Test
    public void shouldCompleted() {
        String id = manager.create(SeededDatabase.ID_PENDING_TRANSACTION_1);

        doReturn(Flux.just(id))
                .when(eventRepository)
                        .findAllIdsForApplying(any(BalanceChangeEventStatus.class),
                                anyInt(),
                                anyString(),
                                any(LocalDateTime.class));


        doReturn(Mono.just(Set.of(id)))
                .when(serviceController)
                .getAppliableBalanceEvents(any(GetProcessedBalanceEventIdsRequest.class));

        v1Module.clearPendings().block();


        verify(eventRepository).bulkStatusUpdate(anyCollection(),
                any(BalanceChangeEventStatus.class));

        var event1 = manager.get(id);
        Assertions.assertEquals(BalanceChangeEventStatus.COMPLETED, event1.getStatus());





    }
}
