package com.panut.centralbanking.transactionservice.application.transactions.pipeline.data.create;

import com.panut.centralbanking.transactionservice.core.TransactionLineDirection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransactionLineCreateData {
    private String accountId;
    private TransactionLineDirection direction;
    private BigDecimal basedAmount;
    private BigDecimal convertedAmount;
    private String ledgerAccountId;
}
