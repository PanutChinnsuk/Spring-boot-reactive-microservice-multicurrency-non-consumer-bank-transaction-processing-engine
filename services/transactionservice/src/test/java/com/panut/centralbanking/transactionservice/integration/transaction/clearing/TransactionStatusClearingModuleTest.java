package com.panut.centralbanking.transactionservice.integration.transaction.clearing;


import com.panut.centralbanking.appcore.integrationtest.SeededDatabase;
import com.panut.centralbanking.appcore.integrationtest.SeededDatabaseIdFlag;
import com.panut.centralbanking.transactionservice.IntegrationDevTest;
import com.panut.centralbanking.transactionservice.application.transactions.clearing.schedules.TransactionStatusClearingScheduler;
import com.panut.centralbanking.transactionservice.application.transactions.clearing.schedules.modules.TransactionStatusClearingModuleV1;
import com.panut.centralbanking.transactionservice.core.TransactionStatus;
import com.panut.centralbanking.transactionservice.core.TransactionType;
import com.panut.centralbanking.transactionservice.infrastructure.model.Transaction;
import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionProfileRepository;
import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionRepository;
import com.panut.centralbanking.transactionservice.integration.transaction.cleanrepo.RepositoryCleanUp;
import com.panut.centralbanking.transactionservice.integration.transaction.isolation.TransactionISO;
import com.panut.centralbanking.transactionservice.testrepository.TransactionProfileTestRepository;
import com.panut.centralbanking.transactionservice.testrepository.TransactionTestRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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
    private TransactionISO transactionISO;

    @Autowired
    private TransactionProfileTestRepository profileTestRepository;

    @Autowired
    private RepositoryCleanUp cleanRepo;

    private String transactionPendingAuthorizeId;
    private String transactionPendingPostedId;

    private TransactionStatus pendingAuthorized = TransactionStatus.PENDING_AUTHORIZED;
    private TransactionStatus pendingPosted = TransactionStatus.PENDING_POSTED;


    @BeforeAll
    public void setUp() {
        var pendingAuth = transactionISO
                .transaction(TransactionType.TRANSFER, pendingAuthorized);
        var pendingPost = transactionISO
                .transaction(TransactionType.TRANSFER, pendingPosted);
        transactionISO.transactionProfile(pendingPost);

        transactionPendingAuthorizeId = pendingAuth.getId();
        transactionPendingPostedId = pendingPost.getId();
    }

    @AfterAll
    public void cleanUp() {
        cleanRepo.hardCleanUp(transactionPendingAuthorizeId,
                transactionPendingPostedId);
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
