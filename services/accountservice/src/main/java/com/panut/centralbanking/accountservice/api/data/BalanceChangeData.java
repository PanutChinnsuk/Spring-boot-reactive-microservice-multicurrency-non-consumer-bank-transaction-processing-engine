package com.panut.centralbanking.accountservice.api.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.panut.centralbanking.accountservice.api.data.context.OverdraftChangeContext;
import com.panut.centralbanking.accountservice.core.BalanceChangeType;
import com.panut.centralbanking.accountservice.core.BalanceImpact;
import com.panut.centralbanking.appcore.exceptions.SystemMisused;
import jakarta.annotation.Nullable;
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

    @NotNull
    private LocalDateTime createdAt;

    @NotBlank
    private String transactionId;

    @Nullable
    private OverdraftChangeContext overdraftChangeContext;


    public BalanceChangeData(String accountId,
                             BigDecimal amount,
                             BalanceChangeType balanceChangeType,
                             BalanceImpact balanceImpact,
                             LocalDateTime createdAt,
                             String transactionId) {
        this.accountId = accountId;
        this.amount = amount;
        this.balanceChangeType = balanceChangeType;
        this.balanceImpact = balanceImpact;
        this.createdAt = createdAt;
        this.transactionId = transactionId;
    }


    @JsonIgnore
    public BalanceChangeData withOverdraftChangeContext(final OverdraftChangeContext ctx) {
        if (this.overdraftChangeContext != null)
            throw new SystemMisused("BalanceChangeData OverdraftChangeContext can " +
                    "only be set once.");

        this.overdraftChangeContext = ctx;

        return this;
    }



}
