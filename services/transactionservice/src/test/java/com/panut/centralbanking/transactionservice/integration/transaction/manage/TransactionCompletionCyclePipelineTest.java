package com.panut.centralbanking.transactionservice.integration.transaction.manage;

import com.panut.centralbanking.appcore.exceptions.BusinessException;
import com.panut.centralbanking.appcore.integrationtest.SeededDatabase;
import com.panut.centralbanking.transactionservice.IntegrationDevTest;
import com.panut.centralbanking.transactionservice.api.service.account.data.related.BalanceChangeType;
import com.panut.centralbanking.transactionservice.api.service.account.data.related.BalanceImpact;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.authorization.TransactionAuthorizePipeline;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.posting.TransactionPostPipeline;
import com.panut.centralbanking.transactionservice.core.TransactionStatus;
import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionLine;
import com.panut.centralbanking.transactionservice.infrastructure.model.events.BalanceChangeEvent;
import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionRepository;
import com.panut.centralbanking.transactionservice.integration.transaction.cleanrepo.RepositoryCleanUp;
import com.panut.centralbanking.transactionservice.integration.transaction.cleanrepo.TransactionManageTestRepositoryCleanup;
import com.panut.centralbanking.transactionservice.integration.transaction.isolation.orchest.TransactionCompletionCycleISO;
import com.panut.centralbanking.transactionservice.testrepository.BalanceChangeEventTestRepository;
import com.panut.centralbanking.transactionservice.testrepository.TransactionLineTestRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionCompletionCyclePipelineTest implements IntegrationDevTest {
    @Autowired
    private TransactionAuthorizePipeline transactionAuthorizePipeline;


    @Autowired
    private TransactionRepository repository;

    @Autowired
    private BalanceChangeEventTestRepository eventRepository;

    @Autowired
    private TransactionLineTestRepository lineRepository;

    @Autowired
    private TransactionPostPipeline transactionPostPipeline;

    @Autowired
    private TransactionCompletionCycleISO iso;

    @Autowired
    private RepositoryCleanUp cleanRepo;


    private String no_asset_expense_id;
    private String overdrafted_id;
    private String asset_expense_id;
    private String authorized_no_asset_expense_id;
    private String authorized_asset_expense_id;
    private String authorized_overdraft_id;
    private String reversedTransactionUnsettledChildId;


    private String deductingAccountId = SeededDatabase.ID_NORMAL_ACCOUNT_1;
    private String addingAccountId = SeededDatabase.ID_NORMAL_ACCOUNT_2;
    private int digits = SeededDatabase.DATABASE_DECIMAL_DIGITS;

    private List<String> transactionIds = new ArrayList<>();

    @BeforeAll
    public void setUp() {
        no_asset_expense_id = iso.noAssetExpenseCase(addingAccountId, deductingAccountId,
                TransactionStatus.PENDING, BigDecimal.valueOf(9)).getId();

        asset_expense_id = iso.assetExpenseCase(addingAccountId, deductingAccountId,
                TransactionStatus.PENDING, BigDecimal.valueOf(2.5415)).getId();

        overdrafted_id = iso.overdraftCase(addingAccountId, deductingAccountId,
                TransactionStatus.PENDING, BigDecimal.valueOf(200)).getId();

        authorized_no_asset_expense_id = iso.noAssetExpenseCase(addingAccountId, deductingAccountId,
                TransactionStatus.AUTHORIZED, BigDecimal.valueOf(99.99)).getId();

        authorized_asset_expense_id = iso.assetExpenseCase(addingAccountId, deductingAccountId,
                TransactionStatus.AUTHORIZED, BigDecimal.valueOf(0.021)).getId();

        authorized_overdraft_id = iso.overdraftCase(addingAccountId, deductingAccountId,
                TransactionStatus.AUTHORIZED, BigDecimal.TEN).getId();


        var reversed = iso.reverseWithUnsettledChildCase();

        reversedTransactionUnsettledChildId = reversed.getId();

        transactionIds.add(no_asset_expense_id);
        transactionIds.add(asset_expense_id);
        transactionIds.add(overdrafted_id);
        transactionIds.add(authorized_asset_expense_id);
        transactionIds.add(authorized_no_asset_expense_id);
        transactionIds.add(authorized_overdraft_id);
        transactionIds.add(reversedTransactionUnsettledChildId);
        transactionIds.add(reversed.getReversedOfTransactionId());

    }


    @AfterAll
    public void afterEach() {
        cleanRepo.hardCleanUp(transactionIds);
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
        var result = transactionAuthorizePipeline
                .authorize(no_asset_expense_id).block();

        Assertions.assertEquals(TransactionStatus.PENDING_AUTHORIZED,
                result.getTransactionStatus());

        checkAuthorizeLedger(no_asset_expense_id, deductingAccountId);
    }

    @Test
    public void shouldPendingAuthorizeForOverdraft() {
        var result = transactionAuthorizePipeline
                .authorize(overdrafted_id).block();

        Assertions.assertEquals(TransactionStatus.PENDING_AUTHORIZED,
                result.getTransactionStatus());

        checkAuthorizeLedger(overdrafted_id, deductingAccountId);
    }

    @Test
    public void shouldPendingAuthorizeForAssetExpense() {
        var result = transactionAuthorizePipeline
                .authorize(asset_expense_id).block();

        Assertions.assertEquals(TransactionStatus.PENDING_AUTHORIZED,
                result.getTransactionStatus());

        checkAuthorizeLedger(asset_expense_id, deductingAccountId);
    }

    @Test
    public void shouldPendingPostForNoAssetExpense() {
        var result = transactionPostPipeline
                .post(authorized_no_asset_expense_id).block();

        Assertions.assertEquals(TransactionStatus.PENDING_POSTED,
                result.getTransactionStatus());


        checkPostLedger(authorized_no_asset_expense_id, deductingAccountId, addingAccountId);
    }

    @Test
    public void shouldPendingPostForOverdraft() {
        var result = transactionPostPipeline
                .post(authorized_overdraft_id).block();

        Assertions.assertEquals(TransactionStatus.PENDING_POSTED,
                result.getTransactionStatus());


        checkPostLedger(authorized_overdraft_id, deductingAccountId, addingAccountId);
    }

    @Test
    public void shouldPendingPostForAssetExpense() {
        var result = transactionPostPipeline
                .post(authorized_asset_expense_id).block();

        Assertions.assertEquals(TransactionStatus.PENDING_POSTED,
                result.getTransactionStatus());

        checkPostLedger(authorized_asset_expense_id, deductingAccountId, addingAccountId);
    }

    @Test
    public void shouldNotAllowUnsettledChildForReversedTransaction() {
        Assertions.assertThrows(
                BusinessException.class,
                () -> transactionAuthorizePipeline.authorize(reversedTransactionUnsettledChildId)
                        .block()
        );
    }
}
