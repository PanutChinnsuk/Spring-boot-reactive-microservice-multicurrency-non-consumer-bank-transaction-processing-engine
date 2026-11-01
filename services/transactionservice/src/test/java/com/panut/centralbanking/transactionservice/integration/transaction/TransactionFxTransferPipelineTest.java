package com.panut.centralbanking.transactionservice.integration.transaction;

import com.panut.centralbanking.appcore.exceptions.BusinessException;
import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.transactionservice.IntegrationDevTest;
import com.panut.centralbanking.transactionservice.api.service.account.AccountServiceController;
import com.panut.centralbanking.transactionservice.api.service.account.request.GetAccountsInfoForTransactionRequest;
import com.panut.centralbanking.transactionservice.api.service.fxRate.FxRateServiceController;
import com.panut.centralbanking.transactionservice.api.service.fxRate.request.FxRateGetLatestRequest;
import com.panut.centralbanking.transactionservice.api.service.fxRate.response.FxRateGetLatestResponse;
import com.panut.centralbanking.transactionservice.application.calculations.conversions.FxRateCurrencyConversion;
import com.panut.centralbanking.transactionservice.application.calculations.profits.FxRateProfitCalculator;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.FxTransferPipeline;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.data.ForeignBuyBaseTransferData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.TransactionCreationPipelineResult;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.FxRateData;
import com.panut.centralbanking.transactionservice.core.TransactionLineDirection;
import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionLine;
import com.panut.centralbanking.transactionservice.integration.transaction.utils.InternalDataBuilder;
import com.panut.centralbanking.transactionservice.testrepository.TransactionLineTestRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionFxTransferPipelineTest implements IntegrationDevTest {
    @Autowired
    private TransactionTestRepositoryCleanUp cleanRepo;

    @Autowired
    private FxTransferPipeline fxTransferPipeline;

    @MockitoSpyBean
    private FxRateServiceController fxRateServiceController;

    @MockitoSpyBean
    private AccountServiceController accountServiceController;

    @Autowired
    private TransactionLineTestRepository transactionLineTestRepository;

    private String internalProfitAccountId = "INTERNAL";
    private String internalClearingAccountId = "INTERNAL2";
    private String fromAccountId = "TEST";
    private String toAccountId = "TEST2";


    public ForeignBuyBaseTransferData foreignBuyBaseTransferData(Long amount) {
        return new ForeignBuyBaseTransferData(fromAccountId, toAccountId,
                BigDecimal.valueOf(amount))
                .withInternalFxProfitAccountId(internalProfitAccountId)
                .withInternalFxClearingAccountId(internalClearingAccountId);
    }

    public FxRateGetLatestResponse latestConversion() {
        return new FxRateGetLatestResponse("TEST",
                BigDecimal.valueOf(25),
                BigDecimal.valueOf(50),
                BigDecimal.valueOf(37.5),
                BigDecimal.valueOf(40),
                CurrencyCode.getBaseCurrency());

    }


    @AfterAll
    public void cleanUp() {
        cleanRepo.cleanUp();
    }

    private void test(final Long transferingForeignAmount) {
        ForeignBuyBaseTransferData data = foreignBuyBaseTransferData(transferingForeignAmount);
        FxRateGetLatestResponse latestConversion = latestConversion();
        CurrencyCode quoteCurrency = CurrencyCode.THB;
        FxRateData fxRateData = FxRateData.quoteAsk(quoteCurrency);
        int digits = 4;

        doReturn(Mono.just(InternalDataBuilder.validFxAccountsInfo(quoteCurrency)))
                .when(accountServiceController)
                .getAccountsInfoForTransaction(any(GetAccountsInfoForTransactionRequest.class));
        doReturn(Mono.just(latestConversion))
                .when(fxRateServiceController)
                .getLatestFxRate(any(FxRateGetLatestRequest.class));


        TransactionCreationPipelineResult result =
                fxTransferPipeline.foreignBuyBase(data)
                        .block();

        cleanRepo.transactionId = result.getTransactionId();
        cleanRepo.transactionLineIds = result.getTransactionLineIds();


        BigDecimal profitAmount = FxRateProfitCalculator
                .V1.foreignBuyBaseProfit(data.getForeignAmount(),
                        latestConversion, fxRateData).setScale(digits);
        BigDecimal convertedAmount = FxRateCurrencyConversion
                .V1.convertDivide(data.getForeignAmount(), latestConversion,
                        fxRateData).setScale(digits);

        List<TransactionLine> transactionLines = transactionLineTestRepository
                .findByTransactionId(result.getTransactionId()).collectList()
                .block();


        Map<String, TransactionLine> resultMap = transactionLines
                .stream()
                .collect(Collectors.toMap(
                        list -> list.getAccountId(),
                        Function.identity()
                ));

        TransactionLine profitLine = resultMap.get(internalProfitAccountId);
        TransactionLine toAccountLine = resultMap.get(toAccountId);

        Assertions.assertEquals(profitAmount, profitLine.getConvertedAmount(), "profit equal");
        Assertions.assertEquals(convertedAmount, toAccountLine.getConvertedAmount(), "converted equal");


    }

    @Test
    public void shouldCreateForeignBuyBaseByDefault() {
        test(1000L);
    }

    @Test
    public void foreignBuyBaseShouldForcedThrowForTooLowTransfer() {
        Assertions.assertThrows(BusinessException.class,
                () -> test(0L));
    }
}
