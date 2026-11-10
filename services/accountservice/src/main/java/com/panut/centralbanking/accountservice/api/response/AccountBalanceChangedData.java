package com.panut.centralbanking.accountservice.api.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.panut.centralbanking.accountservice.api.data.AccountOverdraftCreateData;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountBalance;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

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

    private List<AccountOverdraftCreateData> overdraftedFromTransaction;


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
    public AccountBalanceChangedData after(final AccountBalance accountBalance,
                                           final List<AccountOverdraftCreateData> overdraftedFromTransaction) {
        this.afterBalance = accountBalance.getBalance();
        this.afterReserve = accountBalance.getReserve();
        this.afterLedger = accountBalance.getLedger();
        this.afterOverdrafted = accountBalance.getOverdrafted();
        this.overdraftedFromTransaction = overdraftedFromTransaction;
        return this;
    }

    @JsonIgnore
    public AccountBalanceChangedData combineAfter(final AccountBalanceChangedData data) {
        this.afterBalance = this.afterBalance.add(data.getAfterBalance());
        this.afterReserve = this.afterReserve.add(data.getAfterReserve());
        this.afterLedger = this.afterLedger.add(data.getAfterLedger());
        this.afterOverdrafted = this.afterOverdrafted.add(data.getAfterOverdrafted());

        return this;
    }
}
