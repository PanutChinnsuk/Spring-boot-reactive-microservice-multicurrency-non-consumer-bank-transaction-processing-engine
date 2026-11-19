package com.panut.centralbanking.accountservice.application.worker;

import com.panut.centralbanking.accountservice.core.WorkerName;
import com.panut.centralbanking.accountservice.infrastructure.model.worker.AccountServiceWorker;
import com.panut.centralbanking.accountservice.infrastructure.repository.AccountServiceWorkerRepository;
import com.panut.centralbanking.appcore.exceptions.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class WorkerService {
    private final AccountServiceWorkerRepository repository;

    public WorkerService(AccountServiceWorkerRepository repository) {
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
        return repository.save(AccountServiceWorker
                .builder()
                .workerName(workerName)
                .build())
                .then()
                .doOnSuccess(a -> log.info("Added worker name, {}",
                        workerName));
    }
}
