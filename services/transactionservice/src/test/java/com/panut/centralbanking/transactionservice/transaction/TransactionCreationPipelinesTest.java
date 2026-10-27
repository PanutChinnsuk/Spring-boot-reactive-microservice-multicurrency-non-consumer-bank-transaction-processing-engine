package com.panut.centralbanking.transactionservice.transaction;

import com.panut.centralbanking.appcore.exceptions.SystemMisused;
import com.panut.centralbanking.transactionservice.IntegrationDevTest;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.TransactionCreationPipelineResult;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.TransactionRegisterPipeline;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.AccountData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.FxRateData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.PreTransactionRegisterData;
import com.panut.centralbanking.transactionservice.core.TransactionLineDirection;
import com.panut.centralbanking.transactionservice.core.TransactionStatus;
import com.panut.centralbanking.transactionservice.core.TransactionType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionCreationPipelinesTest implements IntegrationDevTest {
    @Autowired
    private TransactionRegisterPipeline transactionRegisterPipeline;

    @Autowired
    private TransactionTestRepositoryCleanUp cleanRepo;



    @AfterAll
    public void cleanUp() {
        cleanRepo.cleanUp();
    }

    public AccountData accountData1() {
        return new AccountData("TEST", BigDecimal.ONE, BigDecimal.ONE,
                "TEST", TransactionLineDirection.CREDIT);
    }

    public AccountData accountData2() {
        return new AccountData("TEST2", BigDecimal.ONE, BigDecimal.ONE,
                "TEST", TransactionLineDirection.DEBIT);
    }

    public List<AccountData> validAccountDatas() {
        return List.of(
                accountData1(),
                accountData2()
        );
    }

    public List<AccountData> unsatisfiedAccountDatas() {
        return List.of(
                accountData1()
        );
    }

    public List<AccountData> unbalancedAccountDatas() {
        return List.of(
                accountData1(),
                accountData1(),
                accountData2()
        );
    }


    public PreTransactionRegisterData preTransactionRegisterData(List<AccountData> accountDatas) {
        return new PreTransactionRegisterData(TransactionType.TRANSFER,
                accountDatas,
                FxRateData.empty());
    }

    @Test
    public void shouldCreateByDefault() {
        TransactionCreationPipelineResult result =
                transactionRegisterPipeline.register(preTransactionRegisterData(validAccountDatas()))
                        .block();

        Assertions.assertEquals(TransactionStatus.PENDING,
                result.getTransactionStatus());
        cleanRepo.transactionId = result.getTransactionId();
        cleanRepo.transactionLineIds = result.getTransactionLineIds();

    }

    @Test
    public void shouldThrowForUnsatisfiedLines() {
        Assertions.assertThrows(SystemMisused.class,
                () -> transactionRegisterPipeline.register(
                        preTransactionRegisterData(unsatisfiedAccountDatas())
                ).block());
    }

    @Test
    public void shouldThrowForUnbalancedLines() {
        Assertions.assertThrows(SystemMisused.class,
                () -> transactionRegisterPipeline.register(
                        preTransactionRegisterData(unbalancedAccountDatas())
                ).block());
    }


}
