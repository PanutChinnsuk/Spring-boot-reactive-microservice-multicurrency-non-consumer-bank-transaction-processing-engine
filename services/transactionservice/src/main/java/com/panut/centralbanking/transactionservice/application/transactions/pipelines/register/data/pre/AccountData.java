package com.panut.centralbanking.transactionservice.application.transactions.pipelines.register.data.pre;

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
    private final String ledgerCode;
    private final TransactionLineDirection direction;
    private final CurrencyCode accountCurrency;
}
