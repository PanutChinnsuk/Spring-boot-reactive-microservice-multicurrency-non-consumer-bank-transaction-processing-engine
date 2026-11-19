package com.panut.centralbanking.accountservice.infrastructure.repository;


import com.panut.centralbanking.accountservice.core.WorkerName;
import com.panut.centralbanking.accountservice.infrastructure.model.worker.AccountServiceWorker;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface AccountServiceWorkerRepository extends
        ReactiveCrudRepository<AccountServiceWorker, String> {

    @Query("""
            SELECT w.* FROM account_service_workers w
            WHERE w.worker_name = :workerName 
            FOR UPDATE
            """)
    Mono<AccountServiceWorker> getWorker(@Param("workerName")WorkerName workerName);
}
