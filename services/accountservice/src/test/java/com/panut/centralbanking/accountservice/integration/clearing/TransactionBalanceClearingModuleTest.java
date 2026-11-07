package com.panut.centralbanking.accountservice.integration.clearing;


import com.panut.centralbanking.accountservice.IntegrationDevTest;
import com.panut.centralbanking.accountservice.api.data.BalanceChangeData;
import com.panut.centralbanking.accountservice.api.response.AccountBalanceChangedData;
import com.panut.centralbanking.accountservice.api.service.transaction.TransactionClearingServiceController;
import com.panut.centralbanking.accountservice.application.transactions.accountbalancechange.service.BalanceChangeService;
import com.panut.centralbanking.accountservice.application.transactions.clearing.modules.TransactionBalanceClearingSchedulerModuleV1;
import com.panut.centralbanking.accountservice.core.BalanceChangeType;
import com.panut.centralbanking.accountservice.core.BalanceImpact;
import com.panut.centralbanking.accountservice.infrastructure.repository.AccountBalanceRepository;
import com.panut.centralbanking.accountservice.integration.accounts.AccountBalanceCleanUpRepository;
import com.panut.centralbanking.appcore.common.response.CommonResult;
import com.panut.centralbanking.appcore.integrationtest.SeededDatabase;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.config.Task;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.event.annotation.AfterTestMethod;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.Mockito.*;

@Disabled("low stake NOT VERY mandatory")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionBalanceClearingModuleTest implements IntegrationDevTest {
    @MockitoSpyBean
    private TransactionBalanceClearingSchedulerModuleV1 v1Module;

    @Autowired
    private AccountBalanceRepository repository;

    @Autowired
    private AccountBalanceCleanUpRepository cleanRepo;

    @MockitoSpyBean
    private TransactionClearingServiceController transactionController;

    @MockitoSpyBean
    private BalanceChangeService balanceChangeService;

    String accountId = SeededDatabase.ID_NORMAL_ACCOUNT_1;

    private Flux<BalanceChangeData> balanceChangeDataFlux(BigDecimal amount) {
        return Flux.just(
                new BalanceChangeData(accountId,
                        amount,
                        BalanceChangeType.BALANCE,
                        BalanceImpact.DEDUCT,
                        LocalDateTime.now(),
                        null),
                new BalanceChangeData(accountId,
                        amount,
                        BalanceChangeType.RESERVE,
                        BalanceImpact.ADD,
                        LocalDateTime.now(),
                        null)
        );
    }


    @AfterAll
    public void setBack() {
        cleanRepo.setBack(accountId);
    }

    @Test
    public void shouldProcess() throws InterruptedException {
        BigDecimal amount = BigDecimal.ONE
                .setScale(SeededDatabase.DATABASE_DECIMAL_DIGITS);
        Flux<BalanceChangeData> flux = balanceChangeDataFlux(amount);

        doReturn(flux)
                .when(transactionController)
                .getBulkPending();

        doReturn(balanceChangeService.bulk(flux.collectList().block(),
                true).subscribe())
                .when(v1Module).process(anyList());

        var preProcess = repository.findById(accountId).block();
        BigDecimal preBalance = preProcess.getBalance();
        BigDecimal preReserve = preProcess.getReserve();

        var clear = v1Module.clearPending().block();


        Thread.sleep(4000);

        var postProcess = repository.findById(accountId).block();
        BigDecimal postBalance = postProcess.getBalance();
        BigDecimal postReserve = postProcess.getReserve();

        Assertions.assertEquals(amount, preBalance.subtract(postBalance), "balance");
        Assertions.assertEquals(amount, postReserve.subtract(preReserve), "reserve");
    }
}
