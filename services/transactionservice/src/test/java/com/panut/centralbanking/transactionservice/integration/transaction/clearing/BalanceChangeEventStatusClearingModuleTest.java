package com.panut.centralbanking.transactionservice.integration.transaction.clearing;

import com.panut.centralbanking.appcore.integrationtest.SeededDatabase;
import com.panut.centralbanking.transactionservice.IntegrationDevTest;
import com.panut.centralbanking.transactionservice.api.service.account.AccountBalanceChangeServiceController;
import com.panut.centralbanking.transactionservice.api.service.account.request.GetProcessedBalanceEventIdsRequest;
import com.panut.centralbanking.transactionservice.application.events.balancechange.BalanceChangeEventRepositoryManager;
import com.panut.centralbanking.transactionservice.application.transactions.clearing.schedules.modules.BalanceChangeEventStatusClearingModuleV1;
import com.panut.centralbanking.transactionservice.application.transactions.clearing.service.BalanceChangeEventClearingQueryService;
import com.panut.centralbanking.transactionservice.application.worker.WorkerService;
import com.panut.centralbanking.transactionservice.core.BalanceChangeEventStatus;
import com.panut.centralbanking.transactionservice.core.WorkerName;
import com.panut.centralbanking.transactionservice.infrastructure.model.Transaction;
import com.panut.centralbanking.transactionservice.infrastructure.repository.BalanceChangeEventRepository;
import com.panut.centralbanking.transactionservice.integration.transaction.cleanrepo.RepositoryCleanUp;
import com.panut.centralbanking.transactionservice.integration.transaction.clearing.manager.TestEventManager;
import com.panut.centralbanking.transactionservice.integration.transaction.isolation.BalanceChangeEventISO;
import com.panut.centralbanking.transactionservice.integration.transaction.isolation.TransactionISO;
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
    private AccountBalanceChangeServiceController serviceController;

    @Autowired
    private BalanceChangeEventStatusClearingModuleV1 v1Module;

    @MockitoSpyBean
    private BalanceChangeEventRepository eventRepository;

    @Autowired
    private TransactionISO transactionISO;

    @Autowired
    private BalanceChangeEventISO eventISO;

    @Autowired
    private RepositoryCleanUp cleanRepo;

    @MockitoSpyBean
    private WorkerService workerService;


    private Transaction transaction;

    @BeforeAll
    public void setUp() {
        transaction = transactionISO.transaction();

    }

    @AfterAll
    public void cleanUp() {
        cleanRepo.hardCleanUp(transaction.getId());
    }


    @Test
    public void shouldCompleted() {
        String eventId = eventISO.balanceChangeEvent(transaction).getId();

        doReturn(Flux.just(eventId))
                .when(eventRepository)
                        .findAllIdsForApplying(any(BalanceChangeEventStatus.class),
                                anyInt(),
                                anyString(),
                                any(LocalDateTime.class));


        doReturn(Mono.just(Set.of(eventId)))
                .when(serviceController)
                .getAppliableBalanceEvents(any(GetProcessedBalanceEventIdsRequest.class));

        v1Module.clearPendings().block();


        verify(eventRepository).bulkStatusUpdate(anyCollection(),
                any(BalanceChangeEventStatus.class));
        verify(workerService).initWorker(any(WorkerName.class));

        var event = eventRepository.findById(eventId).block();
        Assertions.assertEquals(BalanceChangeEventStatus.COMPLETED, event.getStatus());





    }
}
