package com.panut.centralbanking.accountservice.integration.accounts;

import com.panut.centralbanking.accountservice.IntegrationDevTest;
import com.panut.centralbanking.accountservice.api.data.BalanceChangeData;
import com.panut.centralbanking.accountservice.api.response.AccountBalanceChangedData;
import com.panut.centralbanking.accountservice.application.accounts.account.query.AccountQuery;
import com.panut.centralbanking.accountservice.application.transactions.accountbalancechange.service.BalanceChangeService;
import com.panut.centralbanking.accountservice.core.BalanceChangeType;
import com.panut.centralbanking.accountservice.core.BalanceImpact;
import com.panut.centralbanking.accountservice.infrastructure.model.Account;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountBalance;
import com.panut.centralbanking.accountservice.infrastructure.repository.AccountBalanceRepository;
import com.panut.centralbanking.appcore.common.response.CommonResult;
import com.panut.centralbanking.appcore.integrationtest.SeededDatabase;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountBalanceChangeServiceTest implements IntegrationDevTest {
    @Autowired
    private AccountBalanceRepository accountBalanceRepository;

    @Autowired
    private BalanceChangeService balanceChangeService;

    @Autowired
    private AccountQuery accountQuery;

    @Autowired
    private AccountBalanceCleanUpRepository cleanRepo;

    private String account1 = SeededDatabase.ID_NORMAL_ACCOUNT_1;
    private String account2 = SeededDatabase.ID_NORMAL_ACCOUNT_2;

    private List<String> accountIds = new ArrayList<>();

    private BalanceChangeData balanceChangeData(String accountId,
                                                BigDecimal amount,
                                                BalanceImpact balanceImpact) {
        BalanceChangeData data = new BalanceChangeData();
        data.setAccountId(accountId);
        data.setBalanceChangeType(BalanceChangeType.BALANCE);
        data.setAmount(amount);
        data.setBalanceImpact(balanceImpact);

        return data;
    }

    private BalanceChangeData balanceChangeData(String accountId,
                                               BigDecimal amount,
                                               BalanceChangeType balanceChangeType,
                                                BalanceImpact balanceImpact) {
        BalanceChangeData data = balanceChangeData(accountId, amount, balanceImpact);
        data.setBalanceChangeType(balanceChangeType);

        return data;
    }




    @AfterAll
    public void setBack() {
        cleanRepo.setBack(account1, account2);
    }

    @Test
    public void shouldAddBalance() {
        List<BalanceChangeData> single = List.of(balanceChangeData(account1,
                BigDecimal.ONE, BalanceImpact.ADD));

        CommonResult result = balanceChangeService
                .bulk(single, true).block();
        List<AccountBalanceChangedData> resultList = (List<AccountBalanceChangedData>)
                result.getOptionalBody();
        AccountBalanceChangedData data = resultList.getFirst();

        BigDecimal balanceAdded = single.getFirst()
                .getAmount().add(data.getBeforeBalance());
        BigDecimal ledgerAdded = data.getAfterBalance().add(data.getAfterReserve());

        Assertions.assertTrue(result.isSuccess());
        Assertions.assertEquals(balanceAdded, data.getAfterBalance());
        Assertions.assertEquals(data.getAfterLedger(), ledgerAdded);

    }

    @Test
    public void shouldBalanceToReserve() {
        List<BalanceChangeData> single = List.of( balanceChangeData(account1,
                        BigDecimal.ONE,
                        BalanceChangeType.BALANCE,
                        BalanceImpact.DEDUCT),
                balanceChangeData(account1,
                        BigDecimal.ONE,
                        BalanceChangeType.RESERVE,
                        BalanceImpact.ADD));

        CommonResult result = balanceChangeService
                .bulk(single, true).block();
        List<AccountBalanceChangedData> resultList = (List<AccountBalanceChangedData>)
                result.getOptionalBody();
        Assertions.assertEquals(1, resultList.size());
        AccountBalanceChangedData data = resultList.getFirst();

        BalanceChangeData deduct = single.get(0);
        BalanceChangeData add = single.get(1);

        Assertions.assertTrue(result.isSuccess());

        BigDecimal balanceSubtracted = data.getBeforeBalance().subtract(deduct.getAmount());
        BigDecimal reserveAdded = data.getBeforeReserve().add(add.getAmount());

        Assertions.assertEquals(balanceSubtracted, data.getAfterBalance());
        Assertions.assertEquals(reserveAdded, data.getAfterReserve());
        Assertions.assertEquals(data.getBeforeLedger(), data.getAfterLedger());

    }

    @Test
    public void shouldTransfer() {
        List<BalanceChangeData> single = List.of( balanceChangeData(account1,
                        BigDecimal.TEN,
                        BalanceChangeType.BALANCE,
                        BalanceImpact.DEDUCT),
                balanceChangeData(account2,
                        BigDecimal.TEN,
                        BalanceChangeType.BALANCE,
                        BalanceImpact.ADD));

        CommonResult result = balanceChangeService
                .bulk(single, true).block();
        List<AccountBalanceChangedData> resultList = (List<AccountBalanceChangedData>)
                result.getOptionalBody();
        Assertions.assertEquals(2, resultList.size());
        AccountBalanceChangedData data = resultList.getFirst();

        BalanceChangeData deduct = single.get(0);
        BalanceChangeData add = single.get(1);
        AccountBalanceChangedData deductFrom = resultList
                .stream().filter(a -> a.getAccountId()
                        .equals(account1)).toList().getFirst();
        AccountBalanceChangedData transferTo = resultList
                .stream().filter(a -> a.getAccountId()
                        .equals(account2)).toList().getFirst();

        Assertions.assertTrue(result.isSuccess());

        BigDecimal deductFromBalanceSubtracted = deductFrom
                .getBeforeBalance().subtract(deduct.getAmount());
        BigDecimal transferToBalanceAdded = transferTo
                .getBeforeBalance().add(add.getAmount());


        Assertions.assertEquals(deductFromBalanceSubtracted,
                deductFrom.getAfterBalance());
        Assertions.assertEquals(transferToBalanceAdded,
                transferTo.getAfterBalance());



    }








}
