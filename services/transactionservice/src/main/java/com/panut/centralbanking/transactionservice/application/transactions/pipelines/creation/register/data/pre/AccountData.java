package com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre;

import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.transactionservice.core.TransactionLineDirection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
public class AccountData {
    private final String accountId;
    private final BigDecimal amount;
    private final BigDecimal convertedAmount;
    private final String ledgerAccountId;
    private final TransactionLineDirection direction;

}
