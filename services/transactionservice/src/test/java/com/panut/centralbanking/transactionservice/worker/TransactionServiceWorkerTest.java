package com.panut.centralbanking.transactionservice.worker;


import com.panut.centralbanking.transactionservice.IntegrationDevTest;
import com.panut.centralbanking.transactionservice.application.worker.WorkerService;
import com.panut.centralbanking.transactionservice.core.WorkerName;
import com.panut.centralbanking.transactionservice.integration.transaction.isolation.WorkerISO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionServiceWorkerTest implements IntegrationDevTest {
    @Autowired
    private WorkerService workerService;

    @Autowired
    private WorkerISO workerISO;

    private WorkerName workerName = WorkerName.TRANSACTION;

    @BeforeAll
    public void setUp() {
        workerISO.worker(workerName);
    }

    @Test
    public void shouldExcess() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);


        Future<Void> c1 = executorService.submit(
                () -> workerService.initWorker(workerName).block()
        );

        Future<Void> c2 = executorService.submit(
                () -> workerService.initWorker(workerName).block()
        );



       try {
           c1.get();
           c2.get();
       }catch (ExecutionException e) {

           Assertions.assertEquals(OptimisticLockingFailureException.class,
                   e.getCause().getClass());
       }
    }
}
