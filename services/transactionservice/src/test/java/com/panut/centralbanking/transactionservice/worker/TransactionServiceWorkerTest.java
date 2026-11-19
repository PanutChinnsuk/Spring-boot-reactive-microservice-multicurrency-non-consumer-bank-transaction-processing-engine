package com.panut.centralbanking.transactionservice.worker;


import com.panut.centralbanking.transactionservice.IntegrationDevTest;
import com.panut.centralbanking.transactionservice.application.worker.WorkerService;
import com.panut.centralbanking.transactionservice.core.WorkerName;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class TransactionServiceWorkerTest implements IntegrationDevTest {
    @Autowired
    private WorkerService workerService;



    @Test
    public void shouldExcess() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);


        Future<Void> c1 = executorService.submit(
                () -> workerService.initWorker(WorkerName.TRANSACTION).block()
        );

        Future<Void> c2 = executorService.submit(
                () -> workerService.initWorker(WorkerName.TRANSACTION).block()
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
