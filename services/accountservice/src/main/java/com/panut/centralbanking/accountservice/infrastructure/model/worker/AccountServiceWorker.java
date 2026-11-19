package com.panut.centralbanking.accountservice.infrastructure.model.worker;

import com.panut.centralbanking.accountservice.core.WorkerName;
import com.panut.centralbanking.appR2dbccore.persistence.VersionedUUIDStringEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("account_service_workers")
@NoArgsConstructor
@Getter
public class AccountServiceWorker extends VersionedUUIDStringEntity {
    @Column("worker_name")
    private WorkerName workerName;

    @Column("last_worked")
    private LocalDateTime lastWorked;


    @Builder
    public AccountServiceWorker(WorkerName workerName) {
        this.workerName = workerName;
    }

    public AccountServiceWorker applyLastWorked() {
        this.lastWorked = LocalDateTime.now();

        return this;
    }

}
