package com.panut.centralbanking.transactionservice.infrastructure.model.worker;


import com.panut.centralbanking.appR2dbccore.persistence.VersionedUUIDStringEntity;
import com.panut.centralbanking.transactionservice.core.WorkerName;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("transaction_service_workers")
@NoArgsConstructor
@Getter
public class TransactionServiceWorker extends VersionedUUIDStringEntity {
    @Column("worker_name")
    private WorkerName workerName;

    @Column("last_worked")
    private LocalDateTime lastWorked;


    @Builder
    public TransactionServiceWorker(WorkerName workerName) {
        this.workerName = workerName;
    }

    public TransactionServiceWorker applyLastWorked() {
        this.lastWorked = LocalDateTime.now();

        return this;
    }

}
