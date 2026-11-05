package com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage;

import com.panut.centralbanking.transactionservice.core.TransactionStatus;
import com.panut.centralbanking.transactionservice.infrastructure.model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransactionManagePipelineResult {
    private String transactionId;
    private TransactionStatus transactionStatus;



    public TransactionManagePipelineResult transaction(final Transaction transaction) {
        this.transactionId = transaction.getId();
        this.transactionStatus = transaction.getTransactionStatus();

        return this;
    }
}
