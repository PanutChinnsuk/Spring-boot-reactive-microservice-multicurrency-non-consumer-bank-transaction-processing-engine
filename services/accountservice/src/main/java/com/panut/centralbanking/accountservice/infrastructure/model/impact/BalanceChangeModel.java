package com.panut.centralbanking.accountservice.infrastructure.model.impact;

import com.panut.centralbanking.accountservice.core.BalanceChangeType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface BalanceChangeModel {
    String getAccountId();
    BigDecimal getAmount();
    BalanceChangeType getBalanceChangeType();
    LocalDateTime getChangedTimestamp();
    String getTransactionId();
    Boolean isDeduct();


}
