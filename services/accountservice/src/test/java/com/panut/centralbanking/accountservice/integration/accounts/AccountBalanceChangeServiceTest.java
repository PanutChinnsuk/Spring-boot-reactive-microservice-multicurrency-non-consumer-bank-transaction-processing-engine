package com.panut.centralbanking.accountservice.integration.accounts;

import com.panut.centralbanking.accountservice.IntegrationDev;
import com.panut.centralbanking.accountservice.api.data.BalanceChangeData;
import com.panut.centralbanking.accountservice.api.response.AccountBalanceChangedData;
import com.panut.centralbanking.accountservice.application.account.query.AccountQuery;
import com.panut.centralbanking.accountservice.application.accountbalance.AccountBalanceRepositoryManager;
import com.panut.centralbanking.accountservice.application.balancechangemodel.service.BalanceChangeService;
import com.panut.centralbanking.accountservice.core.BalanceChangeType;
import com.panut.centralbanking.accountservice.infrastructure.model.Account;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountBalance;
import com.panut.centralbanking.accountservice.infrastructure.repository.AccountBalanceRepository;
import com.panut.centralbanking.accountservice.infrastructure.repository.AccountRepository;
import com.panut.centralbanking.appcore.exceptions.Forbidden;
import com.panut.centralbanking.appcore.exceptions.SystemMisused;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountBalanceChangeServiceTest implements IntegrationDev {
    @Autowired
    private AccountBalanceRepository accountBalanceRepository;

    @Autowired
    private BalanceChangeService balanceChangeService;

    @Autowired
    private AccountQuery accountQuery;


    private String accountId;

    private BalanceChangeData balanceChangeData(String accountId,
                                               BigDecimal amount) {
        BalanceChangeData data = new BalanceChangeData();
        data.setAccountId(accountId);
        data.setBalanceChangeType(BalanceChangeType.BALANCE);
        data.setAmount(amount);

        return data;
    }

    private BalanceChangeData balanceChangeData(String accountId,
                                               BigDecimal amount,
                                               BalanceChangeType balanceChangeType) {
        BalanceChangeData data = balanceChangeData(accountId, amount);
        data.setBalanceChangeType(balanceChangeType);

        return data;
    }


    private List<BalanceChangeData> invalidList() {
        return List.of(balanceChangeData("a", BigDecimal.ONE),
                balanceChangeData("b", BigDecimal.ONE));
    }

    private Account account() {
        Account account = accountQuery.findGeneric("TEST").block();
        if (this.accountId == null) this.accountId = account.getId();

        return account;
    }

    private AccountBalance accountBalance() {
        Account account = account();

        return accountBalanceRepository.findById(account.getId()).block();
    }

    @AfterAll
    public void setBack() {
        if (accountId == null) return;

        AccountBalance accountBalance = accountBalanceRepository
                .findById(accountId).block();

        accountBalance.deductSimple(accountBalance.getBalance(),
                BalanceChangeType.BALANCE);
        accountBalance.deductSimple(accountBalance.getReserve(),
                BalanceChangeType.RESERVE);

        accountBalance.addSimple(BigDecimal.valueOf(100L),
                BalanceChangeType.BALANCE);



        accountBalanceRepository.save(accountBalance).block();
    }


    @Test
    @Order(1)
    public void shouldEnforcedSameAccountId() {
        Assertions.assertThrows(SystemMisused.class,
                () -> balanceChangeService.add(invalidList()).block());
    }

    @Test
    @Order(2)
    public void shouldAddBalance() {
        BigDecimal before = accountBalance().getBalance();
        List<BalanceChangeData> single = List.of(balanceChangeData(accountId,
                BigDecimal.ONE));
        AccountBalanceChangedData result = balanceChangeService
                .add(single).block().getOptionalBody();

        Assertions.assertEquals(before,result.getBeforeBalance());
        Assertions.assertEquals(single.getFirst()
                .getAmount().add(before), result.getAfterBalance());

    }

    @Test
    @Order(3)
    public void shouldBoth() {
        BigDecimal before = accountBalance().getBalance();
        List<BalanceChangeData> add = List.of(balanceChangeData(accountId,
                BigDecimal.ONE));
        List<BalanceChangeData> deduct = List.of(balanceChangeData(accountId,
                BigDecimal.ONE));


    }
}
