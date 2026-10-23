package com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.create.data;

import com.panut.centralbanking.transactionservice.core.TransactionLineDirection;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class TransactionLineCreateData {
    private final String accountId;
    private final TransactionLineDirection direction;
    private final BigDecimal basedAmount;
    private final BigDecimal convertedAmount;
    private final String ledgerAccountId;
}
