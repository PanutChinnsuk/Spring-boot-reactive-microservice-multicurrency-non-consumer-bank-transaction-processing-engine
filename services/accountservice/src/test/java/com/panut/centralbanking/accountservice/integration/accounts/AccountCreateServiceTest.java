package com.panut.centralbanking.accountservice.integration.accounts;

import com.panut.centralbanking.accountservice.IntegrationDevTest;
import com.panut.centralbanking.accountservice.api.data.AccountBalanceCreateData;
import com.panut.centralbanking.accountservice.api.data.AccountProfileCreateData;
import com.panut.centralbanking.accountservice.api.response.AccountCreatedData;
import com.panut.centralbanking.accountservice.application.accounts.account.service.AccountCreateService;
import com.panut.centralbanking.accountservice.core.AccountStatus;
import com.panut.centralbanking.accountservice.core.AccountType;
import com.panut.centralbanking.accountservice.infrastructure.repository.AccountBalanceRepository;
import com.panut.centralbanking.accountservice.infrastructure.repository.AccountProfileRepository;
import com.panut.centralbanking.accountservice.infrastructure.repository.AccountRepository;
import com.panut.centralbanking.appcore.integrationtest.SeededDatabase;
import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.reactive.TransactionalOperator;

import java.math.BigDecimal;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountCreateServiceTest implements IntegrationDevTest {
    @Autowired
    private TransactionalOperator transactionalOperator;

    @Autowired
    private AccountCreateService accountCreateService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountProfileRepository accountProfileRepository;

    @Autowired
    private AccountBalanceRepository accountBalanceRepository;

    private AccountProfileCreateData accountProfileCreateData() {
       AccountProfileCreateData data = new AccountProfileCreateData();
       data.setAccountType(AccountType.BANK);
       data.setCurrencyCode(CurrencyCode.USD);
       return data;
    }

    private AccountBalanceCreateData accountBalanceCreateData() {
        AccountBalanceCreateData data = new AccountBalanceCreateData();
        data.setDefaultSeededBalance(BigDecimal.valueOf(100L));
        return data;
    }

    private String accountId;

    @AfterAll
    public void cleanUp() {
        if (accountId == null) return;;

        accountBalanceRepository.deleteById(accountId).block();
        accountProfileRepository.deleteById(accountId).block();
        accountRepository.deleteById(accountId).block();
    }

    @Test
    public void shouldCreateAccount() {
        AccountCreatedData data = accountCreateService
                .createAccount(SeededDatabase.ID_CUSTOMER_1, accountProfileCreateData(),
                        accountBalanceCreateData()).block();


        Assertions.assertEquals(data.getLedger(),
                data.getBalance().add(data.getReserve()),
                "Ledger != balance + reserve");
        Assertions.assertEquals(AccountStatus.ACTIVE,
                data.getAccountStatus(),
                "Default account status != AccountStatus.ACTIVE");
    }
}
