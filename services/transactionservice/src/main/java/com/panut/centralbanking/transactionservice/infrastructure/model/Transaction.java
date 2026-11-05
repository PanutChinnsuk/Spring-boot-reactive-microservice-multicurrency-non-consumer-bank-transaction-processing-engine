package com.panut.centralbanking.transactionservice.infrastructure.model;

import com.panut.centralbanking.appR2dbccore.persistence.VersionedUUIDStringEntity;
import com.panut.centralbanking.transactionservice.core.TransactionStatus;
import com.panut.centralbanking.transactionservice.core.TransactionType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("transactions")
@NoArgsConstructor
@Getter
public class Transaction extends VersionedUUIDStringEntity {
    @Column("type")
    private TransactionType transactionType;

    @Column("status")
    private TransactionStatus transactionStatus;

    @Column("created_at")
    private LocalDateTime createdAt;


    @Builder
    public Transaction(TransactionType transactionType) {
        this.transactionType = transactionType;
        this.transactionStatus = TransactionStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        setInsert();
    }

    public Transaction upNextTransactionStatus() {
        this.transactionStatus = this.transactionStatus.getUpnextStatus();

        return this;
    }

    public Transaction changeStatus(final TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;

        return this;
    }
}
