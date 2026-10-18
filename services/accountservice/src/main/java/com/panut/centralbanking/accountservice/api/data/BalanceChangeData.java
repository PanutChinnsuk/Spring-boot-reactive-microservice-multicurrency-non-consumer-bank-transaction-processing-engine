package com.panut.centralbanking.accountservice.api.data;

import com.panut.centralbanking.accountservice.core.BalanceChangeType;
import com.panut.centralbanking.accountservice.core.BalanceImpact;
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
    @NotBlank
    private String accountId;

    @NotNull
    @PositiveOrZero
    private BigDecimal amount;

    @NotNull
    private BalanceChangeType balanceChangeType;

    @NotNull
    private BalanceImpact balanceImpact;

    private LocalDateTime at = LocalDateTime.now();
    private String transactionId;



}
