package com.panut.centralbanking.accountservice.integration.isolation;

import com.panut.centralbanking.accountservice.infrastructure.model.Account;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountBalance;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountProfile;
import com.panut.centralbanking.accountservice.infrastructure.repository.AccountRepository;
import com.panut.centralbanking.accountservice.integration.testrepository.AccountBalanceTestRepository;
import com.panut.centralbanking.appcore.integrationtest.SeededDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AccountISO {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountBalanceTestRepository balanceTestRepository;

    private String customerId = SeededDatabase.ID_CUSTOMER_1;

    public Account account() {
        var acc = Account.builder()
                .customerId(customerId)
                .build();
        return accountRepository.save(acc).block();
    }


    public AccountBalance accountBalance(Account account,
                                         BigDecimal balance) {
       var accB = AccountBalance.builder()
               .accountId(account.getId())
               .balance(balance)
               .build();

       return balanceTestRepository.save(accB).block();

    }

    public Account accountWithBalance(BigDecimal balance) {
        var acc = account();
        accountBalance(acc, balance);

        return acc;
    }
}
