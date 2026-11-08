package com.panut.centralbanking.transactionservice.integration.transaction;

import com.panut.centralbanking.appcore.exceptions.BusinessException;
import com.panut.centralbanking.appcore.integrationtest.SeededDatabase;
import com.panut.centralbanking.transactionservice.IntegrationDevTest;
import com.panut.centralbanking.transactionservice.api.request.SameCurrencyTransferRequest;
import com.panut.centralbanking.transactionservice.api.service.account.AccountServiceController;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.TransactionCreationPipelineResult;
import com.panut.centralbanking.transactionservice.application.transactions.transaction.implement.CommonTransactionServiceImpl;
import com.panut.centralbanking.transactionservice.application.transactions.transaction.service.CommonTransactionService;
import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionLine;
import com.panut.centralbanking.transactionservice.testrepository.TransactionLineTestRepository;
import com.panut.centralbanking.transactionservice.integration.transaction.utils.AccountControllerDataBuilder;
import com.panut.centralbanking.transactionservice.integration.transaction.utils.DataBuilder;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import reactor.core.publisher.Mono;


import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionCommonTransferServiceTest implements IntegrationDevTest {
    @Autowired
    private CommonTransactionServiceImpl commonTransactionService;

    @MockitoSpyBean
    private AccountServiceController accountServiceController;

    @Autowired
    private TransactionLineTestRepository transactionLineTestRepository;


    @Autowired
    private TransactionTestRepositoryCleanUp cleanUpRepo;






    public SameCurrencyTransferRequest sameCurrencyTransferRequest() {
        return new SameCurrencyTransferRequest(SeededDatabase.ID_NORMAL_ACCOUNT_1,
                SeededDatabase.ID_NORMAL_ACCOUNT_2,
                BigDecimal.ONE);
    }


    @AfterEach
    public void cleanUp() {
        cleanUpRepo.cleanUp();
    }



    private void test(Function<SameCurrencyTransferRequest, Mono<TransactionCreationPipelineResult>> f,
                      SameCurrencyTransferRequest r) {


        TransactionCreationPipelineResult result = f.apply(r).block();

        List<TransactionLine> afterResult = transactionLineTestRepository
                .findByTransactionId(result.getTransactionId()).collectList()
                .block();

        Assertions.assertEquals(2, afterResult.size());
        TransactionLine first = afterResult.getFirst();
        TransactionLine last = afterResult.getLast();

        Assertions.assertEquals(first.getBasedAmount(), first.getConvertedAmount());
        Assertions.assertEquals(last.getBasedAmount(), last.getConvertedAmount());


        cleanUpRepo.transactionId = result.getTransactionId();
        cleanUpRepo.transactionLineIds = result.getTransactionLineIds();
    }

    private void accountServiceControllerReturnValid() {
        AccountControllerDataBuilder.controllerReturn(
                DataBuilder.validAccountsInfo(), accountServiceController);
    }

    @Test
    public void shouldAllowedSameCurrencyTransfer() {
        accountServiceControllerReturnValid();
        test(commonTransactionService::sameCurrencyTransfer,
                sameCurrencyTransferRequest());
    }

    @Test
    public void shouldAllowedSameCurrencyExpense() {
        accountServiceControllerReturnValid();
        test(commonTransactionService::sameCurrencyExpense,
                sameCurrencyTransferRequest());
    }

    @Test
    public void shouldAllowedSameCurrencyAsset() {
        accountServiceControllerReturnValid();
        test(commonTransactionService::sameCurrencyAsset,
                sameCurrencyTransferRequest());
    }

    @Test
    public void shouldNotAllowNonActiveAccounts() {
        AccountControllerDataBuilder.controllerReturn(
                DataBuilder.terminatedAccountsInfo(), accountServiceController);

        Assertions.assertThrows(BusinessException.class,
                () -> test(commonTransactionService::sameCurrencyTransfer,
                        sameCurrencyTransferRequest()));
    }

    @Test
    public void shouldNotAllowDifferentCurrency() {
        AccountControllerDataBuilder.controllerReturn(
                DataBuilder.differentCurrencyAccountsInfo(), accountServiceController);

        Assertions.assertThrows(BusinessException.class,
                () -> test(commonTransactionService::sameCurrencyTransfer,
                        sameCurrencyTransferRequest()));
    }

}
