package com.panut.centralbanking.transactionservice.integration.transaction.manage;


import com.panut.centralbanking.appcore.integrationtest.SeededDatabase;
import com.panut.centralbanking.transactionservice.IntegrationDevTest;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.reverse.TransactionReversePipeline;
import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionLine;
import com.panut.centralbanking.transactionservice.integration.transaction.cleanrepo.TransactionTestRepositoryCleanUp;
import com.panut.centralbanking.transactionservice.testrepository.TransactionLineTestRepository;
import com.panut.centralbanking.transactionservice.testrepository.TransactionProfileTestRepository;
import com.panut.centralbanking.transactionservice.testrepository.TransactionTestRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Predicate;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionReverseCycleTest implements IntegrationDevTest {
    @Autowired
    private TransactionReversePipeline reversePipeline;

    @Autowired
    private TransactionTestRepositoryCleanUp cleanRepo;

    @Autowired
    private TransactionProfileTestRepository profileTestRepository;

    @Autowired
    private TransactionTestRepository  transactionTestRepository;

    @Autowired
    private TransactionLineTestRepository lineTestRepository;

    private String settledTransactionId = SeededDatabase.ID_SETTLED_TRANSACTION;
    private String settledTransactionWithFxRateId = SeededDatabase
            .ID_SETTLED_TRANSACTION_WITH_FX_RATE_ID;

    private BigDecimal getTotal(List<TransactionLine> lines,
                                Predicate<TransactionLine> predicate) {
        var total = lines.stream()
                .filter(predicate)
                .map(TransactionLine::getConvertedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return total;
    }

    private void test(String settledTransactionId) {
        List<TransactionLine> child = lineTestRepository
                .findByTransactionId(settledTransactionId)
                .collectList()
                .block();

        var profile = profileTestRepository.findById(settledTransactionId).block();

        var totalCredit = getTotal(child,
                t -> t.getDirection().isCreditSide());
        var totalDebit = getTotal(child,
                t -> t.getDirection().isDebitSide());

        var justClearingCredit = getTotal(child,
                t -> t.getDirection().isClearingCredit());
        var justClearingDebit = getTotal(child,
                t -> t.getDirection().isClearingDebit());

        Assertions.assertTrue(totalCredit.signum() > 0, "cred");
        Assertions.assertTrue(totalDebit.signum() > 0, "deb");
        Assertions.assertTrue(justClearingCredit.signum() > 0, "cl cred");
        Assertions.assertTrue(justClearingDebit.signum() > 0, "cl deb");


        var result = reversePipeline
                .reverse(settledTransactionId).block();
        var newTransactionId = result.getTransactionId();

        List<TransactionLine> newChild = lineTestRepository
                .findByTransactionId(newTransactionId)
                .collectList()
                .block();
        var newProfile = profileTestRepository.findById(newTransactionId).block();


        cleanRepo.transactionIds.add(result.getTransactionId());
        cleanRepo.transactionLineIds.addAll(newChild.stream()
                .map(TransactionLine::getId).toList());

        var newCredit = getTotal(newChild,
                t -> t.getDirection().isCreditSide());
        var newDebit = getTotal(newChild,
                t -> t.getDirection().isDebitSide());


        Assertions.assertEquals(totalCredit, newDebit, "cred new deb");
        Assertions.assertEquals(totalDebit, newCredit, "deb new cred");
        Assertions.assertEquals(newProfile.getReversedOfTransactionId(), settledTransactionId);
        Assertions.assertEquals(newProfile.getFxDirection(), profile.getFxDirection());


        if (profile.getFxRateId() != null) {

            Assertions.assertEquals(newProfile.getReversedOfTransactionId(),
                    settledTransactionId);
        }
    }


    @AfterEach
    public void cleanUp() {
        cleanRepo.cleanUp();
    }


    @Test
    public void shouldReverse() {
        test(settledTransactionId);


    }

    @Test
    public void shouldReverseWithSameFxRateId() {
        test(settledTransactionWithFxRateId);


    }


}
