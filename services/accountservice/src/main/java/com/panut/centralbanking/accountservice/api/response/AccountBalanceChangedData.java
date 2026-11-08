package com.panut.centralbanking.accountservice.api.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountBalance;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountBalanceChangedData {
    private String accountId;

    private BigDecimal beforeBalance;
    private BigDecimal beforeReserve;
    private BigDecimal beforeLedger;
    private BigDecimal beforeOverdrafted;

    private BigDecimal afterBalance;
    private BigDecimal afterReserve;
    private BigDecimal afterLedger;
    private BigDecimal afterOverdrafted;


    @JsonIgnore
    public AccountBalanceChangedData before(final AccountBalance accountBalance) {
        this.accountId = accountBalance.getId();
        this.beforeBalance = accountBalance.getBalance();
        this.beforeReserve = accountBalance.getReserve();
        this.beforeLedger = accountBalance.getLedger();
        this.beforeOverdrafted = accountBalance.getOverdrafted();
        return this;
    }


    @JsonIgnore
    public AccountBalanceChangedData after(final AccountBalance accountBalance) {
        this.afterBalance = accountBalance.getBalance();
        this.afterReserve = accountBalance.getReserve();
        this.afterLedger = accountBalance.getLedger();
        this.afterOverdrafted = accountBalance.getOverdrafted();
        return this;
    }
}
