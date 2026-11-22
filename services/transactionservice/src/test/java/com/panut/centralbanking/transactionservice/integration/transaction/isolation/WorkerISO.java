package com.panut.centralbanking.transactionservice.integration.transaction.isolation;

import com.panut.centralbanking.transactionservice.application.worker.WorkerService;
import com.panut.centralbanking.transactionservice.core.WorkerName;
import com.panut.centralbanking.transactionservice.infrastructure.model.worker.TransactionServiceWorker;
import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionServiceWorkerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WorkerISO {
    @Autowired
    private TransactionServiceWorkerRepository workerRepository;


    public TransactionServiceWorker worker(WorkerName workerName) {
        var worker = TransactionServiceWorker
                .builder()
                .workerName(workerName)
                .build();

        try {
            return workerRepository.save(worker).block();
        }catch (Exception e) {
            log.info("Already init");

        }

        return worker;
    }
}
