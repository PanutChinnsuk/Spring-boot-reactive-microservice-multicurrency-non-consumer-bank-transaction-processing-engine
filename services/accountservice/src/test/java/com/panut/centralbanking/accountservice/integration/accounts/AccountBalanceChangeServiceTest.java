package com.panut.centralbanking.accountservice.integration.accounts;

import com.panut.centralbanking.accountservice.IntegrationDevTest;
import com.panut.centralbanking.accountservice.api.data.BalanceChangeData;
import com.panut.centralbanking.accountservice.api.response.AccountBalanceChangedData;
import com.panut.centralbanking.accountservice.application.accounts.account.query.AccountQuery;
import com.panut.centralbanking.accountservice.application.transactions.accountbalancechange.implement.BalanceChangeServiceImpl;
import com.panut.centralbanking.accountservice.application.transactions.accountbalancechange.service.BalanceChangeService;
import com.panut.centralbanking.accountservice.core.BalanceChangeType;
import com.panut.centralbanking.accountservice.core.BalanceImpact;
import com.panut.centralbanking.accountservice.infrastructure.model.Account;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountBalance;
import com.panut.centralbanking.accountservice.infrastructure.model.worker.AccountServiceWorker;
import com.panut.centralbanking.accountservice.infrastructure.repository.AccountBalanceRepository;
import com.panut.centralbanking.accountservice.integration.accounts.matcher.AccountBalanceMatcher;
import com.panut.centralbanking.accountservice.integration.cleanup.RepositoryCleanUp;
import com.panut.centralbanking.accountservice.integration.isolation.AccountISO;
import com.panut.centralbanking.appcore.common.response.CommonResult;
import com.panut.centralbanking.appcore.integrationtest.SeededDatabase;
import com.panut.centralbanking.appcore.integrationtest.SeededDatabaseIdFlag;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountBalanceChangeServiceTest implements IntegrationDevTest {
    @Autowired
    private AccountBalanceRepository accountBalanceRepository;

    @Autowired
    private BalanceChangeServiceImpl balanceChangeService;

    @Autowired
    private AccountQuery accountQuery;


    @Autowired
    private AccountBalanceMatcher accountBalanceMatcher;


    @Autowired
    private AccountISO iso;

    @Autowired
    private RepositoryCleanUp cleanRepo;

    private String account1;
    private String account2;


    private List<String> accountIds = new ArrayList<>();

    private BalanceChangeData balanceChangeData(String accountId,
                                                BigDecimal amount,
                                                BalanceImpact balanceImpact) {
        BalanceChangeData data = new BalanceChangeData();
        data.setAccountId(accountId);
        data.setBalanceChangeType(BalanceChangeType.BALANCE);
        data.setAmount(amount);
        data.setBalanceImpact(balanceImpact);
        data.setEventId(SeededDatabaseIdFlag.getTestRandomUUIDString());

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



    @BeforeAll
    public void setUp() {
        account1 = iso.accountWithBalance(BigDecimal.valueOf(1000)).getId();
        account2 = iso.accountWithBalance(BigDecimal.valueOf(1000)).getId();

        accountIds.add(account1);
        accountIds.add(account2);
    }

    @AfterAll
    public void cleanUp() {
        cleanRepo.hardDelete(accountIds);
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

        accountBalanceMatcher.checkAccountBalance(data, account1);

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

        accountBalanceMatcher.checkAccountBalance(data, account1);

    }

    @Test
    public void shouldTransfer() {
        List<BalanceChangeData> single = List.of(
                balanceChangeData(account1,
                        BigDecimal.valueOf(5),
                        BalanceChangeType.BALANCE,
                        BalanceImpact.DEDUCT),
                balanceChangeData(account1,
                        BigDecimal.valueOf(5),
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


        BalanceChangeData deduct = single.get(0);
        BalanceChangeData deduct2 = single.get(1);
        BalanceChangeData add = single.get(2);

        AccountBalanceChangedData deductFrom = resultList
                .stream().filter(a -> a.getAccountId()
                        .equals(account1)).toList().getFirst();


        AccountBalanceChangedData transferTo = resultList
                .stream().filter(a -> a.getAccountId()
                        .equals(account2)).toList().getFirst();

        Assertions.assertTrue(result.isSuccess());

        BigDecimal deductFromBalanceSubtracted = deductFrom
                .getBeforeBalance().subtract(deduct.getAmount().add(deduct2.getAmount()));
        BigDecimal transferToBalanceAdded = transferTo
                .getBeforeBalance().add(add.getAmount());


        Assertions.assertEquals(deductFromBalanceSubtracted,
                deductFrom.getAfterBalance());
        Assertions.assertEquals(transferToBalanceAdded,
                transferTo.getAfterBalance());


        accountBalanceMatcher.checkAccountBalance(deductFrom, account1);
        accountBalanceMatcher.checkAccountBalance(transferTo, account2);
    }



    @Test
    public void shouldProcessOnlyOnce() {
        List<BalanceChangeData> data = List.of(balanceChangeData(account1,
                BigDecimal.ONE,
                BalanceImpact.ADD)
        );

        var firstResult = balanceChangeService.bulk(data, true).block();
        Assertions.assertNotNull(firstResult.getOptionalBody());
        var secondResult = balanceChangeService.bulk(data, true).block();
        Assertions.assertNull(secondResult);
    }





}
