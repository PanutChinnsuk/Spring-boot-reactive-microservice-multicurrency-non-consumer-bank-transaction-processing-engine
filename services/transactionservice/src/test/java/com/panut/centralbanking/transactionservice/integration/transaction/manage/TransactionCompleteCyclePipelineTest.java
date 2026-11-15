package com.panut.centralbanking.transactionservice.integration.transaction.manage;

import com.panut.centralbanking.appcore.integrationtest.SeededDatabase;
import com.panut.centralbanking.transactionservice.IntegrationDevTest;
import com.panut.centralbanking.transactionservice.api.service.account.data.related.BalanceChangeType;
import com.panut.centralbanking.transactionservice.api.service.account.data.related.BalanceImpact;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.TransactionManagePipelineResult;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.authorization.TransactionAuthorizePipeline;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.posting.TransactionPostPipeline;
import com.panut.centralbanking.transactionservice.core.TransactionStatus;
import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionLine;
import com.panut.centralbanking.transactionservice.infrastructure.model.events.BalanceChangeEvent;
import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionRepository;
import com.panut.centralbanking.transactionservice.testrepository.BalanceChangeEventTestRepository;
import com.panut.centralbanking.transactionservice.testrepository.TransactionLineTestRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionCompleteCyclePipelineTest implements IntegrationDevTest {
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

    @Autowired
    private TransactionPostPipeline transactionPostPipeline;


    private String no_asset_expense_id = SeededDatabase.ID_TRANSACTION_NO_ASSET_EXPENSE;
    private String asset_expense_id = SeededDatabase.ID_TRANSACTION_ASSET_EXPENSE;
    private String authorized_no_asset_expense_id = SeededDatabase
            .ID_TRANSACTION_AUTHORIZED_NO_ASSET_EXPENSE;
    private String authorized_asset_expense_id = SeededDatabase
            .ID_TRANSACTION_AUTHORIZED_ASSET_EXPENSE;


    private String deductingAccountId = SeededDatabase.ID_NORMAL_ACCOUNT_1;
    private String addingAccountId = SeededDatabase.ID_NORMAL_ACCOUNT_2;
    private int digits = SeededDatabase.DATABASE_DECIMAL_DIGITS;

    @BeforeEach
    public void beforeEach() {
        cleanRepo.setDefault(no_asset_expense_id, TransactionStatus.PENDING);
        cleanRepo.setDefault(asset_expense_id, TransactionStatus.PENDING);
        cleanRepo.setDefault(authorized_no_asset_expense_id, TransactionStatus.AUTHORIZED);
        cleanRepo.setDefault(authorized_asset_expense_id, TransactionStatus.AUTHORIZED);
    }

    @AfterEach
    public void afterEach() {
        cleanRepo.cleanUpBalanceChangeEvents(no_asset_expense_id);
        cleanRepo.cleanUpBalanceChangeEvents(asset_expense_id);
        cleanRepo.cleanUpBalanceChangeEvents(authorized_no_asset_expense_id);
        cleanRepo.cleanUpBalanceChangeEvents(authorized_asset_expense_id);
        beforeEach();
    }

    private BigDecimal getTotalFromLines(String transactionId,
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

    private void checkAuthorizeLedger(String transactionId,
                            String accountId) {
        BigDecimal totalDeduct = getTotalFromLines(transactionId, accountId);
        BigDecimal totalPendingBalanceDeduct = getTotalPendingChange(BalanceImpact.DEDUCT,
                BalanceChangeType.BALANCE, transactionId);
        BigDecimal totalPendingReserveAdd = getTotalPendingChange(BalanceImpact.ADD,
                BalanceChangeType.RESERVE, transactionId);

        Assertions.assertEquals(totalDeduct, totalPendingBalanceDeduct, "balance deduct");
        Assertions.assertEquals(totalDeduct, totalPendingReserveAdd, "reserve add");
    }

    private void checkPostLedger(String transactionId,
                                      String deductingAccountId,
                                 String addingAccountId) {
        BigDecimal totalDeduct = getTotalFromLines(transactionId, deductingAccountId);
        BigDecimal totalAdd = getTotalFromLines(transactionId, addingAccountId);

        BigDecimal totalPendingReserveDeduct = getTotalPendingChange(BalanceImpact.DEDUCT,
                BalanceChangeType.RESERVE, transactionId);
        BigDecimal totalPendingBalanceAdd = getTotalPendingChange(BalanceImpact.ADD,
                BalanceChangeType.BALANCE, transactionId);

        Assertions.assertEquals(totalDeduct, totalPendingReserveDeduct, "reserve deduct");
        Assertions.assertEquals(totalAdd, totalPendingBalanceAdd, "balance add");
    }

    @Test
    public void shouldPendingAuthorizeForNoAssetExpense() {
        TransactionManagePipelineResult result = transactionAuthorizePipeline
                .authorize(no_asset_expense_id).block();

        Assertions.assertEquals(TransactionStatus.PENDING_AUTHORIZED,
                result.getTransactionStatus());

        checkAuthorizeLedger(no_asset_expense_id, deductingAccountId);
    }

    @Test
    public void shouldPendingAuthorizeForAssetExpense() {
        TransactionManagePipelineResult result = transactionAuthorizePipeline
                .authorize(asset_expense_id).block();

        Assertions.assertEquals(TransactionStatus.PENDING_AUTHORIZED,
                result.getTransactionStatus());

        checkAuthorizeLedger(asset_expense_id, deductingAccountId);
    }

    @Test
    public void shouldPendingPostForNoAssetExpense() {
        TransactionManagePipelineResult result = transactionPostPipeline
                .post(authorized_no_asset_expense_id).block();

        Assertions.assertEquals(TransactionStatus.PENDING_POSTED,
                result.getTransactionStatus());

        checkPostLedger(authorized_no_asset_expense_id, deductingAccountId, addingAccountId);
    }

    @Test
    public void shouldPendingPostForAssetExpense() {
        TransactionManagePipelineResult result = transactionPostPipeline
                .post(authorized_asset_expense_id).block();

        Assertions.assertEquals(TransactionStatus.PENDING_POSTED,
                result.getTransactionStatus());

        checkPostLedger(authorized_asset_expense_id, deductingAccountId, addingAccountId);
    }
}
