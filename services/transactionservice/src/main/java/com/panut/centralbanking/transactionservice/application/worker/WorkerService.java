package com.panut.centralbanking.transactionservice.application.worker;


import com.panut.centralbanking.appcore.exceptions.BusinessException;
import com.panut.centralbanking.transactionservice.core.WorkerName;
import com.panut.centralbanking.transactionservice.infrastructure.model.worker.TransactionServiceWorker;
import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionServiceWorkerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class WorkerService {
    private final TransactionServiceWorkerRepository repository;

    public WorkerService(TransactionServiceWorkerRepository repository) {
        this.repository = repository;
    }


    public Mono<Void> initWorker(final WorkerName workerName) {
        return repository.getWorker(workerName)
                .switchIfEmpty(Mono.error(
                        () -> new BusinessException("Worker name of " + workerName +
                                " has not yet been set.")
                ))
                .flatMap(worker ->
                        repository.save(worker.applyLastWorked()))
                .then()
                .doOnSuccess(a -> log.info("Get and init'd worker name, {}",
                        workerName));
    }

    public Mono<Void> addWorker(final WorkerName workerName) {
        return repository.save(TransactionServiceWorker
                .builder()
                .workerName(workerName)
                .build())
                .then()
                .doOnSuccess(a -> log.info("Added worker name, {}",
                        workerName));
    }
}
