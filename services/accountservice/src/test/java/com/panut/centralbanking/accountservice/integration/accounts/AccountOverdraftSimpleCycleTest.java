package com.panut.centralbanking.accountservice.integration.accounts;

import com.panut.centralbanking.accountservice.IntegrationDevTest;
import com.panut.centralbanking.accountservice.api.data.BalanceChangeData;
import com.panut.centralbanking.accountservice.api.response.AccountBalanceChangedData;
import com.panut.centralbanking.accountservice.application.transactions.accountbalancechange.implement.BalanceChangeServiceImpl;
import com.panut.centralbanking.accountservice.core.BalanceChangeType;
import com.panut.centralbanking.accountservice.core.BalanceImpact;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountBalance;
import com.panut.centralbanking.accountservice.infrastructure.repository.AccountOverdraftRepository;
import com.panut.centralbanking.accountservice.integration.testrepository.AccountBalanceTestRepository;
import com.panut.centralbanking.accountservice.integration.testrepository.AccountOverdraftTestRepository;
import com.panut.centralbanking.appcore.integrationtest.SeededDatabase;
import com.panut.centralbanking.appcore.integrationtest.SeededDatabaseIdFlag;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountOverdraftSimpleCycleTest implements IntegrationDevTest {
    @Autowired
    private AccountBalanceCleanUpRepository cleanRepo;

    @Autowired
    private BalanceChangeServiceImpl balanceChangeService;

    @Autowired
    private AccountOverdraftRepository overdraftRepository;

    @Autowired
    private AccountOverdraftTestRepository overdraftTestRepository;

    @Autowired
    private AccountBalanceTestRepository accountBalanceTestRepository;

    private String accountId = SeededDatabase.ID_NORMAL_ACCOUNT_1;
    private BigDecimal amount = BigDecimal.valueOf(77.65);
    private BigDecimal availableBalance = BigDecimal.valueOf(42.05);
    private AccountBalanceChangedData previousResult;
    private int digits = SeededDatabase.DATABASE_DECIMAL_DIGITS;


    private List<BalanceChangeData> addOverdraft(BigDecimal amount) {
        return List.of(
                new BalanceChangeData(accountId,
                        amount,
                        BalanceChangeType.BALANCE,
                        BalanceImpact.DEDUCT,
                        LocalDateTime.now(),
                        null)
        );
    }

    private List<BalanceChangeData> deductOverdraft(BigDecimal amount) {
        return List.of(
                new BalanceChangeData(accountId,
                        amount,
                        BalanceChangeType.OVERDRAFTED,
                        BalanceImpact.DEDUCT,
                        LocalDateTime.now(),
                        null)
        );
    }

    @AfterAll
    public void cleanUp() {
        cleanRepo.setBack(accountId);
        overdraftTestRepository.deleteByAccountId(accountId,
                SeededDatabaseIdFlag.TEST).block();

    }

    @BeforeAll
    public void set() {
        accountBalanceTestRepository.setDefaultBalance(availableBalance,
                List.of(accountId)).block();
    }

    private void checkAccountBalance(AccountBalanceChangedData result) {
        AccountBalance accountBalance = accountBalanceTestRepository
                .findById(accountId).block();

        Assertions.assertEquals(accountBalance.getBalance(),
                result.getAfterBalance());
        Assertions.assertEquals(accountBalance.getLedger(),
                result.getAfterLedger());
        Assertions.assertEquals(accountBalance.getOverdrafted(),
                result.getAfterOverdrafted());

    }

    @Test
    @Order(1)
    public void shouldAddOverdrafted() {
        var data = addOverdraft(amount);

        List<AccountBalanceChangedData> resultList = (List<AccountBalanceChangedData>)
                balanceChangeService.bulk(data, true)
                        .block()
                        .getOptionalBody();
        var result = resultList.getFirst();

        BigDecimal beforebalance = result.getBeforeBalance();
        BigDecimal beforeLedger = result.getBeforeLedger();
        BigDecimal afterOverdrafted = result.getAfterOverdrafted();
        BigDecimal afterLedger = result.getAfterLedger();


        Assertions.assertEquals(afterOverdrafted, amount.subtract(beforebalance),
                "balance check overdraft");
        Assertions.assertEquals(beforeLedger.subtract(amount),
                afterLedger, "ledger check overdraft");
        Assertions.assertEquals(afterOverdrafted,
                amount.subtract(availableBalance)
                        .setScale(digits), "available check last");

        previousResult = result;

        checkAccountBalance(result);
    }



    @Test
    @Order(2)
    public void shouldClearOverdraftedWithNoOverdraftChangeContext() {
        Assertions.assertNotNull(previousResult);

        BigDecimal clearingAmount = previousResult.getAfterOverdrafted();


        var clearing = deductOverdraft(clearingAmount);

        List<AccountBalanceChangedData> resultList = (List<AccountBalanceChangedData>)
                balanceChangeService.bulk(clearing, true)
                        .block()
                        .getOptionalBody();
        var result = resultList.getFirst();

        Assertions.assertEquals(previousResult.getAfterLedger().add(clearingAmount),
                result.getAfterLedger(), "old ledger check new ledger");
        Assertions.assertEquals(previousResult.getBeforeOverdrafted(),
                result.getAfterOverdrafted(),
                "old overdrafted check new overdrafted");

        checkAccountBalance(result);
    }

    @Test
    @Order(3)
    public void shouldClearOverdraftedWithOverpayingAndNoOverdraftChangeContext() {
        BigDecimal overdrafting = BigDecimal.ONE.setScale(digits);
        BigDecimal overpaying = BigDecimal.TEN.setScale(digits);
        accountBalanceTestRepository.setDefaultOverdraft(BigDecimal.ONE,
               BigDecimal.ZERO.subtract(overdrafting),
                List.of(accountId))
                .block();




        BigDecimal clearingAmount = overdrafting.add(overpaying);

        var clearing = deductOverdraft(clearingAmount);

        List<AccountBalanceChangedData> resultList = (List<AccountBalanceChangedData>)
                balanceChangeService.bulk(clearing, true)
                        .block()
                        .getOptionalBody();
        var result = resultList.getFirst();

        Assertions.assertEquals(result.getAfterLedger(),
                overpaying, "after ledger check refund");
        Assertions.assertEquals(BigDecimal.ZERO.setScale(digits),
                result.getAfterOverdrafted());

        checkAccountBalance(result);
    }

}
