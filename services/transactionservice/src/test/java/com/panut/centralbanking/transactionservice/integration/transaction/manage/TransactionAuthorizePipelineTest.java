package com.panut.centralbanking.transactionservice.integration.transaction.manage;

import com.panut.centralbanking.appcore.integrationtest.SeededDatabase;
import com.panut.centralbanking.transactionservice.IntegrationDevTest;
import com.panut.centralbanking.transactionservice.api.service.account.data.related.BalanceChangeType;
import com.panut.centralbanking.transactionservice.api.service.account.data.related.BalanceImpact;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.TransactionManagePipelineResult;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.authorization.TransactionAuthorizePipeline;
import com.panut.centralbanking.transactionservice.core.TransactionStatus;
import com.panut.centralbanking.transactionservice.infrastructure.model.Transaction;
import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionLine;
import com.panut.centralbanking.transactionservice.infrastructure.model.events.BalanceChangeEvent;
import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionRepository;
import com.panut.centralbanking.transactionservice.testrepository.BalanceChangeEventTestRepository;
import com.panut.centralbanking.transactionservice.testrepository.TransactionLineTestRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionAuthorizePipelineTest implements IntegrationDevTest {
    @Autowired
    private TransactionAuthorizePipeline transactionAuthorizePipeline;

    @Autowired
    private TransactionManageTestRepositoryCleanup cleanRepo;

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private BalanceChangeEventTestRepository eventRepository;

    @Autowired
    private TransactionLineTestRepository lineRepository;


    private String no_asset_expense_id = SeededDatabase.ID_TRANSACTION_NO_ASSET_EXPENSE;
    private String asset_expense_id = SeededDatabase.ID_TRANSACTION_ASSET_EXPENSE;
    private String deductingAccountId = SeededDatabase.ID_NORMAL_ACCOUNT_1;
    private int digits = SeededDatabase.DATABASE_DECIMAL_DIGITS;

    @BeforeEach
    public void beforeEach() {
        cleanRepo.setDefault(no_asset_expense_id);
        cleanRepo.setDefault(asset_expense_id);
    }

    @AfterEach
    public void afterEach() {
        cleanRepo.cleanUpBalanceChangeEvents(no_asset_expense_id);
        cleanRepo.cleanUpBalanceChangeEvents(asset_expense_id);
        beforeEach();
    }

    private BigDecimal getTotalDeductFromLines(String transactionId,
                                               String accountId) {
        return lineRepository
                .findByTransactionId(transactionId)
                .collectList().block()
                .stream().filter(line -> line.getAccountId().equals(accountId))
                .map(TransactionLine::getConvertedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(digits);
    }

    private BigDecimal getTotalPendingChange(BalanceImpact balanceImpact,
                                             BalanceChangeType balanceChangeType,
                                             String transactionId) {
        var events = eventRepository
                .findByTransactionId(transactionId)
                .collectList().block();

        return events
                .stream()
                .filter(event -> event.getBalanceImpact().equals(balanceImpact)
                        && event.getBalanceChangeType().equals(balanceChangeType)
                )
                .map(BalanceChangeEvent::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(digits);
    }

    private void checkEqual(String transactionId,
                            String accountId) {
        BigDecimal totalDeduct = getTotalDeductFromLines(transactionId, accountId);
        BigDecimal totalPendingBalanceDeduct = getTotalPendingChange(BalanceImpact.DEDUCT,
                BalanceChangeType.BALANCE, transactionId);
        BigDecimal totalPendingReserveAdd = getTotalPendingChange(BalanceImpact.ADD,
                BalanceChangeType.RESERVE, transactionId);

        Assertions.assertEquals(totalDeduct, totalPendingBalanceDeduct, "balance");
        Assertions.assertEquals(totalDeduct, totalPendingReserveAdd, "reserve");
    }

    @Test
    public void shouldValidForNoAssetExpense() {
        TransactionManagePipelineResult result = transactionAuthorizePipeline
                .authorize(no_asset_expense_id).block();

        Assertions.assertEquals(TransactionStatus.PENDING_AUTHORIZED,
                result.getTransactionStatus());

        checkEqual(no_asset_expense_id, deductingAccountId);
    }

    @Test
    public void shouldValidForAssetExpense() {
        TransactionManagePipelineResult result = transactionAuthorizePipeline
                .authorize(asset_expense_id).block();

        Assertions.assertEquals(TransactionStatus.PENDING_AUTHORIZED,
                result.getTransactionStatus());

        checkEqual(asset_expense_id, deductingAccountId);
    }
}
