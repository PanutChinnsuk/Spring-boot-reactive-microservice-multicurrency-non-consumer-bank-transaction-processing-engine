package com.panut.centralbanking.accountservice.integration.worker;

import com.panut.centralbanking.accountservice.IntegrationDevTest;
import com.panut.centralbanking.accountservice.api.data.BalanceChangeData;
import com.panut.centralbanking.accountservice.application.worker.WorkerService;
import com.panut.centralbanking.accountservice.core.BalanceImpact;
import com.panut.centralbanking.accountservice.core.WorkerName;
import com.panut.centralbanking.appcore.common.response.CommonResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class AccountServiceWorkerTest implements IntegrationDevTest {
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
