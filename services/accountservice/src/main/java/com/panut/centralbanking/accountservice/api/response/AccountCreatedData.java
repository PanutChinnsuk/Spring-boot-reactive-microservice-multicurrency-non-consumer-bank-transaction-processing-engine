package com.panut.centralbanking.accountservice.api.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.panut.centralbanking.accountservice.core.AccountStatus;
import com.panut.centralbanking.accountservice.core.AccountType;
import com.panut.centralbanking.accountservice.infrastructure.model.Account;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountBalance;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountProfile;
import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import reactor.core.publisher.Mono;


import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreatedData {
    private String accountId;

    private CurrencyCode accountCurrency;
    private AccountType accountType;
    private AccountStatus accountStatus;

    private BigDecimal balance;
    private BigDecimal reserve;
    private BigDecimal ledger;



    @JsonIgnore
    public Mono<AccountCreatedData> account(Account account) {
        return Mono.defer(() -> {
            this.accountId = account.getId();

            return Mono.just(this);
        });
    }

    @JsonIgnore
    public Mono<AccountCreatedData> accountProfile(AccountProfile accountProfile) {
        return Mono.defer(() -> {
            this.accountCurrency = accountProfile.getAccountCurrency();
            this.accountType = accountProfile.getAccountType();
            this.accountStatus = accountProfile.getAccountStatus();

            return Mono.just(this);
        });
    }

    @JsonIgnore
    public Mono<AccountCreatedData> accountBalance(AccountBalance accountBalance) {
        return Mono.defer(() -> {
            this.balance = accountBalance.getBalance();
            this.reserve = accountBalance.getReserve();
            this.ledger = accountBalance.getLedger();


            return Mono.just(this);
        });
    }

}
