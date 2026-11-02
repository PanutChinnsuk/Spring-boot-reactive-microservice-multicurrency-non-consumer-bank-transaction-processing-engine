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
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.data.BaseBuyForeignTransferData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.data.ForeignBuyBaseTransferData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.TransactionCreationPipelineResult;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.FxRateData;
import com.panut.centralbanking.transactionservice.core.TransactionLineDirection;
import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionLine;
import com.panut.centralbanking.transactionservice.integration.transaction.utils.InternalDataBuilder;
import com.panut.centralbanking.transactionservice.testrepository.TransactionLineTestRepository;
import org.junit.jupiter.api.*;
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

    public BaseBuyForeignTransferData baseBuyForeignTransferData(Long amount) {
        return new BaseBuyForeignTransferData(fromAccountId, toAccountId,
                BigDecimal.valueOf(amount))
                .withInternalCollectorAccountId(internalProfitAccountId)
                .withInternalClearingAccountId(internalClearingAccountId);
    }


    public FxRateGetLatestResponse latestConversion() {
        return new FxRateGetLatestResponse("TEST",
                BigDecimal.valueOf(25),
                BigDecimal.valueOf(50),
                BigDecimal.valueOf(37.5),
                BigDecimal.valueOf(40),
                CurrencyCode.getBaseCurrency());

    }


    @AfterEach
    public void cleanUp() {
        cleanRepo.cleanUp();
    }

    private void foreignBuyBase(final Long transferingForeignAmount) {
        ForeignBuyBaseTransferData data = foreignBuyBaseTransferData(transferingForeignAmount);
        FxRateGetLatestResponse latestConversion = latestConversion();
        CurrencyCode quoteCurrency = CurrencyCode.THB;
        FxRateData fxRateData = FxRateData.quoteAsk(quoteCurrency);
        int digits = 4;

        doReturn(Mono.just(InternalDataBuilder.foreignBuyBaseAccountsInfo(quoteCurrency)))
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

    private void baseBuyForeign(final Long transferingBaseAmount) {
        BaseBuyForeignTransferData data = baseBuyForeignTransferData(transferingBaseAmount);
        FxRateGetLatestResponse latestConversion = latestConversion();
        CurrencyCode quoteCurrency = CurrencyCode.THB;
        FxRateData fxRateData = FxRateData.quoteBid(quoteCurrency);
        int digits = 4;

        doReturn(Mono.just(InternalDataBuilder.baseBuyForeignAccountsInfo(quoteCurrency)))
                .when(accountServiceController)
                .getAccountsInfoForTransaction(any(GetAccountsInfoForTransactionRequest.class));
        doReturn(Mono.just(latestConversion))
                .when(fxRateServiceController)
                .getLatestFxRate(any(FxRateGetLatestRequest.class));


        TransactionCreationPipelineResult result =
                fxTransferPipeline.baseBuyForeign(data)
                        .block();

        cleanRepo.transactionId = result.getTransactionId();
        cleanRepo.transactionLineIds = result.getTransactionLineIds();


        BigDecimal baseToForeignAmount = FxRateCurrencyConversion
                .V1.convertMultiply(data.getBaseAmount(), latestConversion, fxRateData)
                .setScale(digits);
        BigDecimal baseAmount = data.getBaseAmount().setScale(digits);


        List<TransactionLine> transactionLines = transactionLineTestRepository
                .findByTransactionId(result.getTransactionId()).collectList()
                .block();


        Map<String, TransactionLine> resultMap = transactionLines
                .stream()
                .collect(Collectors.toMap(
                        list -> list.getAccountId(),
                        Function.identity()
                ));


        TransactionLine collectorLine = resultMap.get(data.getInternalCollectorAccountId());
        TransactionLine toAccountLine = resultMap.get(toAccountId);


        Assertions.assertEquals(baseToForeignAmount, toAccountLine.getConvertedAmount(),
                "base to foreign equal");
        Assertions.assertEquals(baseAmount, collectorLine.getConvertedAmount(),
                "collector base equal");


    }

    @Test
    public void shouldCreateForeignBuyBaseByDefault() {
        foreignBuyBase(1000L);
    }

    @Test
    public void foreignBuyBaseShouldForcedThrowForTooLowTransfer() {
        Assertions.assertThrows(BusinessException.class,
                () -> foreignBuyBase(0L));
    }

    @Test
    public void shouldCreateBaseBuyForeignByDefault() {
        baseBuyForeign(1000L);
    }
}
