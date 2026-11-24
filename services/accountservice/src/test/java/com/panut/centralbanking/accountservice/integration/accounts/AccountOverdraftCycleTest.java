package com.panut.centralbanking.accountservice.integration.accounts;

import com.panut.centralbanking.accountservice.IntegrationDevTest;
import com.panut.centralbanking.accountservice.api.data.AccountOverdraftCreateData;
import com.panut.centralbanking.accountservice.api.data.BalanceChangeData;
import com.panut.centralbanking.accountservice.api.data.context.OverdraftChangeContext;
import com.panut.centralbanking.accountservice.api.response.AccountBalanceChangedData;
import com.panut.centralbanking.accountservice.application.accounts.accountoverdraft.AccountOverdraftConnectionFactory;
import com.panut.centralbanking.accountservice.application.transactions.accountbalancechange.implement.BalanceChangeServiceImpl;
import com.panut.centralbanking.accountservice.core.AccountOverdraftStatus;
import com.panut.centralbanking.accountservice.core.BalanceChangeType;
import com.panut.centralbanking.accountservice.core.BalanceImpact;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountBalance;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountBalanceChange;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountOverdraft;
import com.panut.centralbanking.accountservice.infrastructure.repository.AccountOverdraftRepository;
import com.panut.centralbanking.accountservice.integration.accounts.matcher.AccountBalanceMatcher;
import com.panut.centralbanking.accountservice.integration.cleanup.RepositoryCleanUp;
import com.panut.centralbanking.accountservice.integration.isolation.AccountISO;
import com.panut.centralbanking.accountservice.integration.isolation.AccountOverdraftISO;
import com.panut.centralbanking.accountservice.integration.testrepository.AccountBalanceTestRepository;
import com.panut.centralbanking.accountservice.integration.testrepository.AccountOverdraftTestRepository;
import com.panut.centralbanking.appcore.integrationtest.SeededDatabase;
import com.panut.centralbanking.appcore.integrationtest.SeededDatabaseIdFlag;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.Mockito.*;

record Common(BigDecimal totalPaid,
              BigDecimal totalOwned,
              BigDecimal finalTotalOverdrafted,
              AccountBalanceChangedData result,
              List<AccountOverdraft> overdrafts,
              List<AccountOverdraft> preModifiedOverdrafts,
              BigDecimal totalPreModifiedOvedraftsPaid) {}

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
    private AccountBalanceMatcher accountBalanceMatcher;

    @Autowired
    private AccountOverdraftISO iso;

    @Autowired
    private AccountISO accountISO;

    @Autowired
    private RepositoryCleanUp cleanRepo;



    private String accountId;
    private String ovdId1;
    private String ovdId2;
    private List<String> ovdIds = new ArrayList<>();

    private String overdraftAccountOwnerId;
    private BigDecimal totalOverdrafted = BigDecimal.valueOf(50);
    private int digits = SeededDatabase.DATABASE_DECIMAL_DIGITS;

    private BigDecimal size = AccountOverdraftISO.twiceOverdraftedSize;

    private void reset(List<String> ids,
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
                        List.of(overdraftAccountOwnerId)).block();
    }

    private List<BalanceChangeData> clearingData(BigDecimal amount,
                                                BalanceImpact balanceImpact) {
        return List.of(
            new BalanceChangeData(SeededDatabaseIdFlag.getTestRandomUUIDString(),
                    overdraftAccountOwnerId,
                    amount,
                    BalanceChangeType.OVERDRAFTED,
                    balanceImpact,
                    LocalDateTime.now(),
                    null)
                    .withOverdraftChangeContext(
                            new OverdraftChangeContext(ovdIds)
                    )

        );
    }

    private List<BalanceChangeData> clearingDataWithNoValidOverdraftIds(BigDecimal amount,
                                                 BalanceImpact balanceImpact) {
        return List.of(
                new BalanceChangeData(SeededDatabaseIdFlag.getTestRandomUUIDString(),
                        overdraftAccountOwnerId,
                        amount,
                        BalanceChangeType.OVERDRAFTED,
                        balanceImpact,
                        LocalDateTime.now(),
                        null)
                        .withOverdraftChangeContext(
                                new OverdraftChangeContext(List.of(
                                        SeededDatabase.ID_INVALID_1,
                                        SeededDatabase.ID_INVALID_2
                                ))
                        )

        );
    }



    @BeforeEach
    public void perSetUp() {
        ovdIds.clear();
        var acc = accountISO.accountWithBalance(BigDecimal.ZERO);

        var ovd1 = iso.accountOverdraft(acc, totalOverdrafted, BigDecimal.ZERO);
        var ovd2 = iso.accountOverdraft(acc, totalOverdrafted, BigDecimal.ZERO);

        overdraftAccountOwnerId = acc.getId();
        ovdId1 = ovd1.getId();
        ovdId2 = ovd2.getId();

        ovdIds.add(ovdId1);
        ovdIds.add(ovdId2);



        accountId = acc.getId();
    }

    @AfterEach
    public void perCleanup() {
        cleanRepo.hardDelete(accountId);
    }



    @Test
    public void shouldClearWithNoOverpaidAndNotEnough() {
        BigDecimal totalPaid = BigDecimal.ZERO;
        BigDecimal paying = BigDecimal.TEN;

        var list = clearing(totalPaid,
                paying,
                totalOverdrafted,
                clearingData(paying, BalanceImpact.DEDUCT));

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
                totalOverdrafted,
                clearingData(paying, BalanceImpact.DEDUCT));

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
                totalOverdrafted,
                clearingData(paying, BalanceImpact.DEDUCT));

        list.forEach(a -> {
            Assertions.assertEquals(AccountOverdraftStatus.CLEARED,
                    a.getAccountOverdraftStatus());
        });

    }

    @Test
    public void shouldDeductMainOverdraftedInstead() {
        BigDecimal totalPaid = BigDecimal.ZERO;
        BigDecimal paying = totalOverdrafted.multiply(size);

        clearingWithFailsafe(totalPaid,
                paying,
                totalOverdrafted,
                clearingDataWithNoValidOverdraftIds(paying, BalanceImpact.DEDUCT));



    }

    @Test
    public void shouldDeductMainOverdraftedInsteadWithOverpaid() {
        BigDecimal totalPaid = BigDecimal.ZERO;
        BigDecimal overpaid = BigDecimal.valueOf(54);
        BigDecimal paying = totalOverdrafted.multiply(size).add(overpaid);

        clearingWithFailsafe(totalPaid,
                paying,
                totalOverdrafted,
                clearingDataWithNoValidOverdraftIds(paying, BalanceImpact.DEDUCT));


    }

    @Test
    public void shouldAddWithNoOverpaid() {
        BigDecimal currentPaid = BigDecimal.valueOf(75);
        BigDecimal adding = BigDecimal.valueOf(45);

        var list = adding(currentPaid,
                adding,
                totalOverdrafted,
                clearingData(adding, BalanceImpact.ADD));

        list.forEach(a -> {
            Assertions.assertEquals(AccountOverdraftStatus.UNPAID,
                    a.getAccountOverdraftStatus());
        });

    }

    @Test
    public void shouldAddWithOverAdd() {
        BigDecimal currentPaid = BigDecimal.valueOf(75);
        BigDecimal overAdd = BigDecimal.valueOf(112.02);
        BigDecimal adding = BigDecimal.valueOf(45).add(overAdd);

        var list = adding(currentPaid,
                adding,
                totalOverdrafted,
                clearingData(adding, BalanceImpact.ADD));

        list.forEach(a -> {
            Assertions.assertEquals(AccountOverdraftStatus.UNPAID,
                    a.getAccountOverdraftStatus());
        });

    }

    @Test
    public void shouldAddMainOverdraftedInstead() {
        BigDecimal totalPaid = BigDecimal.ZERO;

        BigDecimal adding = totalOverdrafted;

        addingWithFailsafe(totalPaid,
                adding,
                totalOverdrafted,
                clearingDataWithNoValidOverdraftIds(adding, BalanceImpact.ADD));


    }

    private void clearingWithFailsafe(BigDecimal currentPaid,
                                            BigDecimal clearingAmount,
                                            BigDecimal totalOverdrafted,
                                            List<BalanceChangeData> clearingData) {

        var common = getCommon(currentPaid, totalOverdrafted, clearingData);
        var finalTotalOverdrafted = common.finalTotalOverdrafted();
        var result = common.result();

        if (clearingAmount.compareTo(finalTotalOverdrafted) > 0) {
            BigDecimal refunded = clearingAmount.subtract(finalTotalOverdrafted);

            Assertions.assertEquals(result.getAfterOverdrafted(),
                    result.getBeforeOverdrafted().subtract(finalTotalOverdrafted),
                    "overdraft clear");

            Assertions.assertEquals(result.getAfterBalance(),
                    result.getBeforeBalance().add(refunded),
                    "refund check");
        }else {
            Assertions.assertEquals(result.getAfterOverdrafted(),
                    result.getBeforeOverdrafted().subtract(clearingAmount));
        }



        accountBalanceMatcher.checkAccountBalance(result, overdraftAccountOwnerId);

    }

    private void addingWithFailsafe(BigDecimal currentPaid,
                                      BigDecimal addingAmount,
                                      BigDecimal totalOverdrafted,
                                      List<BalanceChangeData> clearingData) {

        var common = getCommon(currentPaid, totalOverdrafted, clearingData);
        var finalTotalOverdrafted = common.finalTotalOverdrafted();
        var result = common.result();

        Assertions.assertEquals(result.getAfterOverdrafted(),
                result.getBeforeOverdrafted().add(addingAmount));



        accountBalanceMatcher.checkAccountBalance(result, overdraftAccountOwnerId);

    }




    private List<AccountOverdraft> clearing(BigDecimal currentPaid,
                                            BigDecimal clearingAmount,
                                            BigDecimal totalOverdrafted,
                                            List<BalanceChangeData> clearingData) {

        var common = getCommon(currentPaid, totalOverdrafted, clearingData);
        var finalTotalOverdrafted = common.finalTotalOverdrafted();
        var totalPaid = common.totalPaid();
        var result = common.result();

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
        return common.overdrafts();
    }

    private List<AccountOverdraft> adding(BigDecimal currentPaid,
                                            BigDecimal addingAmount,
                                            BigDecimal totalOverdrafted,
                                            List<BalanceChangeData> clearingData) {
        var common = getCommon(currentPaid, totalOverdrafted, clearingData);
        var finalTotalOverdrafted = common.finalTotalOverdrafted();
        var totalPaid = common.totalPaid();
        var totalPreModifiedPaid = common.totalPreModifiedOvedraftsPaid();
        var result = common.result();

        Assertions.assertTrue(currentPaid.compareTo(finalTotalOverdrafted) <= 0,
                "currentpaid less or equal final total over drafted");

        if (addingAmount.compareTo(totalPreModifiedPaid) > 0) {
            BigDecimal leftover = addingAmount.subtract(totalPreModifiedPaid);

            var newOverdraftedList = result.getOverdraftedFromTransaction();
            Assertions.assertEquals(1, newOverdraftedList.size());

            var newCreatedOverdrafted = newOverdraftedList
                            .stream().filter(a ->
                            a.getAmount().equals(leftover))
                            .toList().getFirst();

            Assertions.assertNotNull(newCreatedOverdrafted, "new overdrafted " +
                    "not null");



            Assertions.assertEquals(leftover, newCreatedOverdrafted.getAmount(),
                    "new overdrafted amount");
        }else {
            Assertions.assertEquals(totalPaid,
                    totalPreModifiedPaid.subtract(addingAmount),
                    "decrease paid");
            Assertions.assertEquals(result.getAfterOverdrafted(),
                    result.getBeforeOverdrafted().add(addingAmount),
                    "increase overdrafted");
        }


        accountBalanceMatcher.checkAccountBalance(result, overdraftAccountOwnerId);
        return common.overdrafts();
    }

    private Common getCommon(BigDecimal currentPaid,
                             BigDecimal totalOverdrafted,
                             List<BalanceChangeData> clearingData) {
        reset(ovdIds, currentPaid, totalOverdrafted);
        BigDecimal finalTotalOverdrafted = totalOverdrafted.multiply(size)
                .setScale(digits);
        setBalanceOverdrafted(finalTotalOverdrafted.subtract(currentPaid));

        List<AccountOverdraft> preModifiedOverdrafts = testRepository
                .findByIds(ovdIds).collectList().block();

        BigDecimal totalPreModifiedOverdraftsPaid = preModifiedOverdrafts
                .stream().map(AccountOverdraft::getPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var resultList = (List<AccountBalanceChangedData>)
                balanceChangeService.bulk(clearingData, true)
                        .block().getOptionalBody();
        Assertions.assertEquals(1, resultList.size());

        var result = resultList.getFirst();

        List<AccountOverdraft> overdrafts = testRepository
                .findByIds(ovdIds).collectList().block();

        BigDecimal totalOwned = overdrafts
                .stream().map(AccountOverdraft::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPaid = overdrafts
                .stream().map(AccountOverdraft::getPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Assertions.assertEquals(finalTotalOverdrafted, totalOwned,
                "total owned");

        return new Common(totalPaid, totalOwned, finalTotalOverdrafted,
                result, overdrafts, preModifiedOverdrafts,
                totalPreModifiedOverdraftsPaid);
    }

}
