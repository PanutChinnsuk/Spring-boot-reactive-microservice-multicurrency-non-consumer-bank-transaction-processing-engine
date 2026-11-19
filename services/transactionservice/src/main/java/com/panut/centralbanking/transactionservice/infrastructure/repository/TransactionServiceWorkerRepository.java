package com.panut.centralbanking.transactionservice.infrastructure.repository;

import com.panut.centralbanking.transactionservice.core.WorkerName;
import com.panut.centralbanking.transactionservice.infrastructure.model.worker.TransactionServiceWorker;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface TransactionServiceWorkerRepository
extends ReactiveCrudRepository<TransactionServiceWorker, String> {

    @Query("""
            SELECT w.* FROM transaction_service_workers w
            WHERE w.worker_name = :workerName 
            FOR UPDATE
            """)
    Mono<TransactionServiceWorker> getWorker(@Param("workerName") WorkerName workerName);
}
