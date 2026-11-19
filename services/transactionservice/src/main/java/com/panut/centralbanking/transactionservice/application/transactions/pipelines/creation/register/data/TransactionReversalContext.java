package com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransactionReversalContext {
    private String ofTransactionId;


}
