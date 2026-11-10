package com.panut.centralbanking.accountservice.integration.accounts;

import com.panut.centralbanking.accountservice.IntegrationDevTest;
import com.panut.centralbanking.accountservice.api.data.BalanceChangeData;
import com.panut.centralbanking.accountservice.api.data.context.OverdraftChangeContext;
import com.panut.centralbanking.accountservice.api.response.AccountBalanceChangedData;
import com.panut.centralbanking.accountservice.application.transactions.accountbalancechange.implement.BalanceChangeServiceImpl;
import com.panut.centralbanking.accountservice.core.AccountOverdraftStatus;
import com.panut.centralbanking.accountservice.core.BalanceChangeType;
import com.panut.centralbanking.accountservice.core.BalanceImpact;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountOverdraft;
import com.panut.centralbanking.accountservice.infrastructure.repository.AccountOverdraftRepository;
import com.panut.centralbanking.accountservice.integration.accounts.matcher.AccountBalanceMatcher;
import com.panut.centralbanking.accountservice.integration.testrepository.AccountBalanceTestRepository;
import com.panut.centralbanking.accountservice.integration.testrepository.AccountOverdraftTestRepository;
import com.panut.centralbanking.appcore.integrationtest.SeededDatabase;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountOverdraftCycleTest implements IntegrationDevTest {
    @Autowired
    private BalanceChangeServiceImpl balanceChangeService;

    @Autowired
    private AccountOverdraftRepository repository;

    @Autowired
    private AccountOverdraftTestRepository testRepository;

    @Autowired
    private AccountBalanceTestRepository accountBalanceTestRepository;

    @Autowired
    private AccountBalanceCleanUpRepository cleanRepo;

    @Autowired
    private AccountBalanceMatcher accountBalanceMatcher;

    private String overdraftId1 = SeededDatabase
            .ID_ACCOUNT_OVERDRAFT_RECORD_WITH_NO_TRANSACTION_1;
    private String overdraftId2 = SeededDatabase
            .ID_ACCOUNT_OVERDRAFT_RECORD_WITH_NO_TRANSACTION_2;
    private String overdraftAccountOwnerId = SeededDatabase
            .ID_NORMAL_ACCOUNT_1;
    private BigDecimal totalOverdrafted = BigDecimal.valueOf(50);
    private int digits = SeededDatabase.DATABASE_DECIMAL_DIGITS;

    private Set<String> ids = Set.of(overdraftId1, overdraftId2);
    private BigDecimal size = BigDecimal.valueOf(ids.size());

    private Set<String> accountIds = Set.of(overdraftAccountOwnerId);

    private void reset(Set<String> ids,
                       BigDecimal toDefault,
                       BigDecimal total) {
        testRepository.reset(ids, toDefault, total).block();
    }

    private void reset(String id,
                       BigDecimal toDefault,
                       BigDecimal total) {
        testRepository.reset(Set.of(id), toDefault, total).block();
    }

    private void setBalanceOverdrafted(BigDecimal amount) {
        accountBalanceTestRepository
                .setDefaultOverdraft(amount,
                        BigDecimal.ZERO.subtract(amount),
                        accountIds).block();
    }

    public List<BalanceChangeData> clearingData(BigDecimal amount,
                                                BalanceImpact balanceImpact) {
        return List.of(
            new BalanceChangeData(overdraftAccountOwnerId,
                    amount,
                    BalanceChangeType.OVERDRAFTED,
                    balanceImpact,
                    LocalDateTime.now(),
                    null)
                    .withOverdraftChangeContext(
                            new OverdraftChangeContext(List.copyOf(ids))
                    )

        );
    }

    @AfterAll
    public void setBack() {
        cleanRepo.setBack(overdraftAccountOwnerId);
    }

    @AfterEach
    public void cleanUp() {
        reset(ids ,BigDecimal.ZERO, totalOverdrafted);
        testRepository.resetStatus(ids,
                AccountOverdraftStatus.UNPAID).block();
    }



    @Test
    public void shouldClearWithNoOverpaidAndNotEnough() {
        BigDecimal totalPaid = BigDecimal.ZERO;
        BigDecimal paying = BigDecimal.TEN;

        var list = clearing(totalPaid,
                paying,
                totalOverdrafted);

        list.forEach(a -> {
            Assertions.assertEquals(AccountOverdraftStatus.UNPAID,
                    a.getAccountOverdraftStatus());
        });

    }

    @Test
    public void shouldClearWithNoOverpaid() {
        BigDecimal totalPaid = BigDecimal.ZERO;
        BigDecimal paying = totalOverdrafted.multiply(size);

        var list = clearing(totalPaid,
                paying,
                totalOverdrafted);

        list.forEach(a -> {
            Assertions.assertEquals(AccountOverdraftStatus.CLEARED,
                    a.getAccountOverdraftStatus());
        });

    }

    @Test
    public void shouldClearWithOverpaid() {
        BigDecimal totalPaid = BigDecimal.ZERO;
        BigDecimal overpaid = BigDecimal.valueOf(57);
        BigDecimal paying = totalOverdrafted.multiply(size).add(overpaid);

        var list = clearing(totalPaid,
                paying,
                totalOverdrafted);

        list.forEach(a -> {
            Assertions.assertEquals(AccountOverdraftStatus.CLEARED,
                    a.getAccountOverdraftStatus());
        });

    }



    private List<AccountOverdraft> clearing(BigDecimal currentPaid,
                          BigDecimal clearingAmount,
                          BigDecimal totalOverdrafted) {

        reset(ids, currentPaid, totalOverdrafted);
        BigDecimal finalTotalOverdrafted = totalOverdrafted.multiply(size)
                        .setScale(digits);
        setBalanceOverdrafted(finalTotalOverdrafted);

        var clearingData = clearingData(clearingAmount,
                BalanceImpact.DEDUCT);

        var resultList = (List<AccountBalanceChangedData>)
                balanceChangeService.bulk(clearingData, true)
                        .block().getOptionalBody();
        Assertions.assertEquals(1, resultList.size());

        var result = resultList.getFirst();

        List<AccountOverdraft> overdrafts = testRepository
                .findByIds(ids).collectList().block();


        BigDecimal totalOwned = overdrafts
                .stream().map(AccountOverdraft::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPaid = overdrafts
                .stream().map(AccountOverdraft::getPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Assertions.assertEquals(finalTotalOverdrafted, totalOwned,
                "total owned");

        if (clearingAmount.compareTo(finalTotalOverdrafted) > 0) {
            BigDecimal refunded = clearingAmount.subtract(finalTotalOverdrafted);

            Assertions.assertEquals(finalTotalOverdrafted.setScale(digits),
                    totalPaid, "total paid, overpaid");

            Assertions.assertEquals(result.getAfterOverdrafted(),
                    result.getBeforeOverdrafted()
                            .subtract(finalTotalOverdrafted),
                    "overdraft matching, overpaid");

            Assertions.assertEquals(result.getBeforeBalance().add(refunded),
                    result.getAfterBalance(),
                    "refund check");
        }else {
            Assertions.assertEquals(currentPaid.add(clearingAmount).setScale(digits),
                    totalPaid, "total paid, less");
            Assertions.assertEquals(result.getAfterOverdrafted(),
                    result.getBeforeOverdrafted()
                            .subtract(clearingAmount),
                    "overdraft matching, less");
        }


        accountBalanceMatcher.checkAccountBalance(result, overdraftAccountOwnerId);
        return overdrafts;
    }
}
