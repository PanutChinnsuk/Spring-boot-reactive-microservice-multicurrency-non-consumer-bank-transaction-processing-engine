package com.panut.centralbanking.transactionservice.integration.transaction.listener;

import com.panut.centralbanking.transactionservice.IntegrationDevTest;
import com.panut.centralbanking.transactionservice.application.kafkalistener.ProcessedBalanceChangeEventListenerHandler;
import com.panut.centralbanking.transactionservice.application.worker.WorkerService;
import com.panut.centralbanking.transactionservice.core.BalanceChangeEventStatus;
import com.panut.centralbanking.transactionservice.core.WorkerName;
import com.panut.centralbanking.transactionservice.application.kafkalistener.data.JustProcessedBalanceChangeEvents;
import com.panut.centralbanking.transactionservice.infrastructure.repository.BalanceChangeEventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.Set;

import static org.mockito.Mockito.*;

public class ProcessedBalanceChangeEventListenerTest implements IntegrationDevTest {

    @Autowired
    private ProcessedBalanceChangeEventListenerHandler handler;

    @MockitoSpyBean
    private BalanceChangeEventRepository repository;

    @MockitoSpyBean
    private WorkerService workerService;

    @Test
    public void shouldCalledManual() {
        Set<String> ids = Set.of("a");

        handler.clearProcessed(new JustProcessedBalanceChangeEvents(ids)).block();

        verify(workerService).initWorker(any(WorkerName.class));
        verify(repository).bulkStatusUpdate(anyCollection(),
                any(BalanceChangeEventStatus.class));
    }
}
