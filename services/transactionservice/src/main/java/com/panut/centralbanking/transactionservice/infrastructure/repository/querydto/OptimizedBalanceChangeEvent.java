package com.panut.centralbanking.transactionservice.infrastructure.repository.querydto;

import com.panut.centralbanking.transactionservice.api.service.account.data.related.BalanceChangeType;
import com.panut.centralbanking.transactionservice.api.service.account.data.related.BalanceImpact;
import com.panut.centralbanking.transactionservice.core.BalanceChangeEventStatus;
import lombok.*;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptimizedBalanceChangeEvent {
    private String id;
    private String accountId;
    private BigDecimal amount;
    private BalanceImpact balanceImpact;
    private BalanceChangeType balanceChangeType;
    private BalanceChangeEventStatus status;





}
