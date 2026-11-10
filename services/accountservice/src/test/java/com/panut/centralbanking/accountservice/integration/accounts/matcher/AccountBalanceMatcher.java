package com.panut.centralbanking.accountservice.integration.accounts.matcher;

import com.panut.centralbanking.accountservice.api.response.AccountBalanceChangedData;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountBalance;
import com.panut.centralbanking.accountservice.integration.testrepository.AccountBalanceTestRepository;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountBalanceMatcher {
    @Autowired
    private AccountBalanceTestRepository accountBalanceTestRepository;

    public void checkAccountBalance(AccountBalanceChangedData result,
                                     String accountId) {
        AccountBalance accountBalance = accountBalanceTestRepository
                .findById(accountId).block();

        Assertions.assertEquals(accountBalance.getBalance(),
                result.getAfterBalance());
        Assertions.assertEquals(accountBalance.getLedger(),
                result.getAfterLedger());
        Assertions.assertEquals(accountBalance.getOverdrafted(),
                result.getAfterOverdrafted());

    }
}
