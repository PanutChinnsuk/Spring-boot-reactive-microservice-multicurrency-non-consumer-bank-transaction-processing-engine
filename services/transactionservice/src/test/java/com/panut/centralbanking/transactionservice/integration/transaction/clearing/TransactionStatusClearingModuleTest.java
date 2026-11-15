package com.panut.centralbanking.transactionservice.integration.transaction.clearing;


import com.panut.centralbanking.appcore.integrationtest.SeededDatabase;
import com.panut.centralbanking.appcore.integrationtest.SeededDatabaseIdFlag;
import com.panut.centralbanking.transactionservice.IntegrationDevTest;
import com.panut.centralbanking.transactionservice.application.transactions.clearing.schedules.TransactionStatusClearingScheduler;
import com.panut.centralbanking.transactionservice.application.transactions.clearing.schedules.modules.TransactionStatusClearingModuleV1;
import com.panut.centralbanking.transactionservice.core.TransactionStatus;
import com.panut.centralbanking.transactionservice.infrastructure.model.Transaction;
import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionRepository;
import com.panut.centralbanking.transactionservice.testrepository.TransactionTestRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.mockito.Mockito.*;

public class TransactionStatusClearingModuleTest implements IntegrationDevTest {
    @Autowired
    private TransactionStatusClearingModuleV1 v1Module;

    @Autowired
    private TransactionTestRepository testRepository;

    @MockitoSpyBean
    private TransactionRepository transactionRepository;

    private String transactionId =
            SeededDatabase.ID_EMPTY_PENDING_AUTHORIZE_TRANSACTION_1;
    private TransactionStatus transactionStatus = TransactionStatus.PENDING_AUTHORIZED;

    private void setDefault() {
        testRepository.setStatus(transactionStatus,
                transactionId).block();
    }

    @AfterEach
    public void afterEach() {
        setDefault();
    }

    @BeforeEach
    public void beforeEach() {
        setDefault();
    }

    @Test
    public void shouldUpNext() {
        v1Module.clear(transactionStatus,
                SeededDatabaseIdFlag.NONE).block();

        Transaction transaction = testRepository
                .findById(transactionId).block();

        verify(transactionRepository).toNextStatus(any(TransactionStatus.class),
                any(TransactionStatus.class),
                anyList());

        Assertions.assertEquals(transaction.getTransactionStatus(),
                transactionStatus.getUpnextStatus());



    }
}
