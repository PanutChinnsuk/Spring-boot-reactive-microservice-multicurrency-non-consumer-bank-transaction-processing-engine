package com.panut.centralbanking.transactionservice.api.service.account.data;


import com.panut.centralbanking.transactionservice.api.service.account.data.related.BalanceChangeType;
import com.panut.centralbanking.transactionservice.api.service.account.data.related.BalanceImpact;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalanceChangeData {
    private String accountId;
    private BigDecimal amount;
    private BalanceChangeType balanceChangeType;

    private BalanceImpact balanceImpact;

    private LocalDateTime at = LocalDateTime.now();
    private String transactionId;



}
