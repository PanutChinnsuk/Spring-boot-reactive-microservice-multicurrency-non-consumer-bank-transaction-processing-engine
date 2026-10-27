package com.panut.centralbanking.transactionservice.transaction;

import com.panut.centralbanking.appcore.exceptions.BusinessException;
import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.transactionservice.IntegrationDevTest;
import com.panut.centralbanking.transactionservice.api.request.SameCurrencyTransferRequest;
import com.panut.centralbanking.transactionservice.api.service.account.AccountServiceController;
import com.panut.centralbanking.transactionservice.api.service.account.data.related.AccountStatus;
import com.panut.centralbanking.transactionservice.api.service.account.request.GetAccountsInfoForTransactionRequest;
import com.panut.centralbanking.transactionservice.api.service.account.response.GetAccountsInfoForTransactionResponse;
import com.panut.centralbanking.transactionservice.api.service.account.response.queryresult.AccountInfoForTransactionQueryResult;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.TransactionCreationPipelineResult;
import com.panut.centralbanking.transactionservice.application.transactions.transaction.service.CommonTransactionService;
import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionLine;
import com.panut.centralbanking.transactionservice.testrepository.TransactionLineTestRepository;
import com.panut.centralbanking.transactionservice.transaction.utils.AccountControllerDataBuilder;
import com.panut.centralbanking.transactionservice.transaction.utils.DataBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import reactor.core.publisher.Mono;


import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionCommonTransferServiceTest implements IntegrationDevTest {
    @Autowired
    private CommonTransactionService commonTransactionService;

    @MockitoSpyBean
    private AccountServiceController accountServiceController;

    @Autowired
    private TransactionLineTestRepository transactionLineTestRepository;


    @Autowired
    private TransactionTestRepositoryCleanUp cleanUpRepo;






    public SameCurrencyTransferRequest sameCurrencyTransferRequest() {
        return new SameCurrencyTransferRequest("TEST", "TEST2",
                BigDecimal.ONE);
    }


    @AfterAll
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
