package com.panut.centralbanking.accountservice.api.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountBalance;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountBalanceChangedData {
    private BigDecimal beforeBalance;
    private BigDecimal beforeReserve;
    private BigDecimal beforeLedger;

    private BigDecimal afterBalance;
    private BigDecimal afterReserve;
    private BigDecimal afterLedger;


    @JsonIgnore
    public Mono<AccountBalanceChangedData> before(final AccountBalance accountBalance) {
        return Mono.defer(() -> {
            this.beforeBalance = accountBalance.getBalance();
            this.beforeReserve = accountBalance.getReserve();
            this.beforeLedger = accountBalance.getLedger();
            return Mono.just(this);
        });
    }

    @JsonIgnore
    public Mono<AccountBalanceChangedData> after(final AccountBalance accountBalance) {
        return Mono.defer(() -> {
            this.afterBalance = accountBalance.getBalance();
            this.afterReserve = accountBalance.getReserve();
            this.afterLedger = accountBalance.getLedger();
            return Mono.just(this);
        });
    }
}
