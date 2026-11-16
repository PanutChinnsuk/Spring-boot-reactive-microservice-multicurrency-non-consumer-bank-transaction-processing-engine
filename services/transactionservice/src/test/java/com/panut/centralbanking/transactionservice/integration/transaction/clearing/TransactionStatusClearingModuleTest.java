package com.panut.centralbanking.transactionservice.integration.transaction.clearing;


import com.panut.centralbanking.appcore.integrationtest.SeededDatabase;
import com.panut.centralbanking.appcore.integrationtest.SeededDatabaseIdFlag;
import com.panut.centralbanking.transactionservice.IntegrationDevTest;
import com.panut.centralbanking.transactionservice.application.transactions.clearing.schedules.TransactionStatusClearingScheduler;
import com.panut.centralbanking.transactionservice.application.transactions.clearing.schedules.modules.TransactionStatusClearingModuleV1;
import com.panut.centralbanking.transactionservice.core.TransactionStatus;
import com.panut.centralbanking.transactionservice.infrastructure.model.Transaction;
import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionProfileRepository;
import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionRepository;
import com.panut.centralbanking.transactionservice.testrepository.TransactionProfileTestRepository;
import com.panut.centralbanking.transactionservice.testrepository.TransactionTestRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

public class TransactionStatusClearingModuleTest implements IntegrationDevTest {
    @Autowired
    private TransactionStatusClearingModuleV1 v1Module;

    @Autowired
    private TransactionTestRepository testRepository;

    @MockitoSpyBean
    private TransactionRepository transactionRepository;

    @MockitoSpyBean
    private TransactionProfileRepository transactionProfileRepository;



    @Autowired
    private TransactionProfileTestRepository profileTestRepository;

    private String transactionPendingAuthorizeId =
            SeededDatabase.ID_EMPTY_PENDING_AUTHORIZE_TRANSACTION_1;
    private String transactionPendingPostedId =
            SeededDatabase.ID_EMPTY_PENDING_POSTED_TRANSACTION_1;
    private TransactionStatus pendingAuthorized = TransactionStatus.PENDING_AUTHORIZED;
    private TransactionStatus pendingPosted = TransactionStatus.PENDING_POSTED;

    private void setDefault() {
        testRepository.setStatus(pendingAuthorized,
                transactionPendingAuthorizeId).block();
        testRepository.setStatus(pendingPosted,
                transactionPendingPostedId).block();

        profileTestRepository.setNullPosted(transactionPendingPostedId).block();
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
    public void shouldUpNextPendingAuthorize() {
        v1Module.clear(pendingAuthorized,
                SeededDatabaseIdFlag.NONE).block();

        Transaction transaction = testRepository
                .findById(transactionPendingAuthorizeId).block();

        verify(transactionRepository).toNextStatus(any(TransactionStatus.class),
                any(TransactionStatus.class),
                anyList());

        Assertions.assertEquals(transaction.getTransactionStatus(),
                pendingAuthorized.getUpnextStatus());



    }

    @Test
    public void shouldUpNextPendingPosted() {
        v1Module.clear(pendingPosted,
                SeededDatabaseIdFlag.NONE).block();

        Transaction transaction = testRepository
                .findById(transactionPendingPostedId).block();
        var profile = profileTestRepository.findById(transactionPendingPostedId).block();

        verify(transactionRepository).toNextStatus(any(TransactionStatus.class),
                any(TransactionStatus.class),
                anyList());

        verify(transactionProfileRepository).setPosted(anyCollection(),
                any(LocalDateTime.class));

        Assertions.assertEquals(transaction.getTransactionStatus(),
                pendingPosted.getUpnextStatus());
        Assertions.assertNotNull(profile.getPostedAt());



    }
}
