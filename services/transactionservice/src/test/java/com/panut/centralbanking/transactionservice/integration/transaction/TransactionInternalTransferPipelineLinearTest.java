package com.panut.centralbanking.transactionservice.integration.transaction;

import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.transactionservice.IntegrationDevTest;
import com.panut.centralbanking.transactionservice.api.service.account.AccountServiceController;
import com.panut.centralbanking.transactionservice.api.service.account.request.GetAccountsInfoForTransactionRequest;
import com.panut.centralbanking.transactionservice.api.service.account.response.GetAccountsInfoForTransactionResponse;
import com.panut.centralbanking.transactionservice.api.service.fxRate.FxRateServiceController;
import com.panut.centralbanking.transactionservice.api.service.fxRate.request.FxRateGetLatestRequest;
import com.panut.centralbanking.transactionservice.api.service.fxRate.response.FxRateGetLatestResponse;
import com.panut.centralbanking.transactionservice.application.calculations.conversions.FxRateCurrencyConversion;
import com.panut.centralbanking.transactionservice.application.ledgeraccount.LedgerCode;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.InternalTransferPipeline;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.data.LinearInternalTransferPipelineData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.CustomAccountTransferDataPolicy;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.CustomInternalAccountProfitContext;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.CustomPerAccountFxTransferDataPolicy;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.CustomPerAccountTransferDataPolicy;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.v1.data.CustomAccountTransferData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.v1.data.CustomAccountTransferDataFxContext;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.TransactionCreationPipelineResult;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.FxRateData;
import com.panut.centralbanking.transactionservice.core.FxDirection;
import com.panut.centralbanking.transactionservice.core.TransactionLineDirection;
import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionLine;
import com.panut.centralbanking.transactionservice.integration.transaction.utils.DataBuilder;
import com.panut.centralbanking.transactionservice.integration.transaction.utils.FxDataBuilder;
import com.panut.centralbanking.transactionservice.testrepository.TransactionLineTestRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionInternalTransferPipelineLinearTest implements IntegrationDevTest {
    @Autowired
    private InternalTransferPipeline internalTransferPipeline;

    @Autowired
    private TransactionTestRepositoryCleanUp cleanRepo;

    @MockitoSpyBean
    private AccountServiceController accountServiceController;

    @MockitoSpyBean
    private FxRateServiceController fxRateServiceController;

    @Autowired
    private TransactionLineTestRepository transactionLineTestRepository;


    private String fromAccountId = "TEST";
    private String toAccountId = "TEST2";
    private String internal1 = "INTERNAL";
    private String internal2 = "INTERNAL2";

    private List<CustomAccountTransferDataPolicy> defaultTransferPolicy() {
        return List.of(
                CustomAccountTransferDataPolicy.ACTIVE_ACCOUNTS_ONLY
        );
    }

    private Set<CustomPerAccountTransferDataPolicy> defaultPerAccountolicy() {
        return Set.of(
                CustomPerAccountTransferDataPolicy.IS_BASE_CURRENCY
        );
    }

    private CustomAccountTransferData customAccountTransferData(String accountId,
                                                                BigDecimal amount,
                                                                TransactionLineDirection direction) {
        return new CustomAccountTransferData(accountId,
                amount,
                null,
                LedgerCode.COMMON_TRANSFER,
                direction,
                null,
                null,
                null);
    }

    private CustomAccountTransferData customAccountTransferData(String accountId,
                                                                BigDecimal amount,
                                                                BigDecimal convertedAmount,
                                                                TransactionLineDirection direction) {
        return new CustomAccountTransferData(accountId,
                amount,
                convertedAmount,
                LedgerCode.COMMON_TRANSFER,
                direction,
                null,
                null,
                null);
    }

    private LinearInternalTransferPipelineData linearAtoB() {
        List<CustomAccountTransferData> perAccounts = List.of(
                customAccountTransferData(fromAccountId, BigDecimal.ONE,
                        TransactionLineDirection.DEBIT)
                        .withPerAccountPolicies(defaultPerAccountolicy()),
                customAccountTransferData(toAccountId, BigDecimal.ONE,
                        TransactionLineDirection.CREDIT)
                        .withPerAccountPolicies(defaultPerAccountolicy())
        );

        return new LinearInternalTransferPipelineData(perAccounts, defaultTransferPolicy());
    }

    private LinearInternalTransferPipelineData linearBaseBuyForeignWithConvertedAmount(BigDecimal baseAmount,
                                                                                 BigDecimal convertedAmount) {
        List<CustomAccountTransferData> perAccounts = List.of(
               customAccountTransferData(fromAccountId, baseAmount,
                       TransactionLineDirection.DEBIT)
                       .withPerAccountPolicies(defaultPerAccountolicy()),

                customAccountTransferData(toAccountId, baseAmount,
                        convertedAmount,
                        TransactionLineDirection.CREDIT),

                customAccountTransferData(internal1, baseAmount,
                        convertedAmount,
                        TransactionLineDirection.CLEARING_DEBIT),

                customAccountTransferData(internal2, baseAmount,
                        TransactionLineDirection.CLEARING_CREDIT)
        );

        return new LinearInternalTransferPipelineData(perAccounts, defaultTransferPolicy());
    }

    private LinearInternalTransferPipelineData linearBaseBuyForeign(BigDecimal baseAmount,
                                                                    FxDirection fxDirection) {
        List<CustomAccountTransferData> perAccounts = List.of(
                customAccountTransferData(fromAccountId, baseAmount,
                        TransactionLineDirection.DEBIT)
                        .withPerAccountPolicies(defaultPerAccountolicy()),

                customAccountTransferData(toAccountId, baseAmount,
                        TransactionLineDirection.CREDIT)
                        .withFxContext(new CustomAccountTransferDataFxContext(
                                fxDirection,
                                CustomPerAccountFxTransferDataPolicy.BASE_BUY_FOREIGN
                        )),

                customAccountTransferData(internal1, baseAmount,
                        TransactionLineDirection.CLEARING_DEBIT)
                        .withFxContext(new CustomAccountTransferDataFxContext(
                                fxDirection,
                                CustomPerAccountFxTransferDataPolicy.BASE_BUY_FOREIGN
                        )),

                customAccountTransferData(internal2, baseAmount,
                        TransactionLineDirection.CLEARING_CREDIT)
                        .withPerAccountPolicies(defaultPerAccountolicy())
        );

        return new LinearInternalTransferPipelineData(perAccounts, defaultTransferPolicy());
    }

    private LinearInternalTransferPipelineData linearForeignBuyBase(BigDecimal foreignAmount,
                                                                    FxDirection fxDirection) {
        List<CustomAccountTransferData> perAccounts = List.of(
                customAccountTransferData(fromAccountId, foreignAmount,
                        TransactionLineDirection.DEBIT)
                        .withFxContext(new CustomAccountTransferDataFxContext(
                                fxDirection,
                                CustomPerAccountFxTransferDataPolicy.FOREIGN_BUY_BASE
                        )),

                customAccountTransferData(toAccountId, foreignAmount,
                        TransactionLineDirection.CREDIT)
                        .withFxContext(new CustomAccountTransferDataFxContext(
                                fxDirection,
                                CustomPerAccountFxTransferDataPolicy.FOREIGN_BUY_BASE
                        )),

                customAccountTransferData(internal1, foreignAmount,
                        TransactionLineDirection.CREDIT)
                        .withFxContext(new CustomAccountTransferDataFxContext(
                                fxDirection,
                                CustomPerAccountFxTransferDataPolicy.FOREIGN_BUY_BASE
                        ))
                        .withProfitContext(new CustomInternalAccountProfitContext(foreignAmount)),

                customAccountTransferData(internal2, foreignAmount,
                        TransactionLineDirection.CLEARING_DEBIT)
                        .withFxContext(new CustomAccountTransferDataFxContext(
                                fxDirection,
                                CustomPerAccountFxTransferDataPolicy.FOREIGN_BUY_BASE
                        ))
                        .withProfitContext(new CustomInternalAccountProfitContext(foreignAmount))
        );

        return new LinearInternalTransferPipelineData(perAccounts, defaultTransferPolicy());
    }




    private Map<String, TransactionLine> getTransactionLineMap(String transactionId) {
        return transactionLineTestRepository
                .findByTransactionId(transactionId)
                .collectList().block()
                .stream()
                .collect(Collectors.toMap(
                        a -> a.getAccountId(),
                        Function.identity()
                ));
    }


    private Map<String, CustomAccountTransferData> getCustomAccountTransferDataMap
            (LinearInternalTransferPipelineData data) {
        return data.getCustomDatas()
                .stream()
                .collect(Collectors.toMap(
                        a -> a.getAccountId(),
                        Function.identity()
                ));
    }

    @AfterEach
    public void cleanUp() {
        cleanRepo.cleanUp();
    }

    @Test
    public void shouldCreateLinearNonFx() {
        LinearInternalTransferPipelineData data = linearAtoB();
        GetAccountsInfoForTransactionResponse response =
                DataBuilder.validAccountsInfo();
        int digits = 4;

        doReturn(Mono.just(response))
                .when(accountServiceController)
                .getAccountsInfoForTransaction(any(GetAccountsInfoForTransactionRequest.class));

        TransactionCreationPipelineResult result = internalTransferPipeline
                .customLinearTransfer(data).block();

        cleanRepo.transactionId = result.getTransactionId();
        cleanRepo.transactionLineIds = result.getTransactionLineIds();

        Map<String, TransactionLine> transactionLineMap = getTransactionLineMap(result.getTransactionId());
        Map<String, CustomAccountTransferData> customAccountTransferDataMap =
                getCustomAccountTransferDataMap(data);


        TransactionLine fromAccountLine = transactionLineMap.get(fromAccountId);
        TransactionLine toAccountLine = transactionLineMap.get(toAccountId);
        var fromAccountData = customAccountTransferDataMap.get(fromAccountId);
        var toAccountData = customAccountTransferDataMap.get(toAccountId);

        Assertions.assertEquals(fromAccountData.getBaseAmount().setScale(digits), fromAccountLine.getConvertedAmount());
        Assertions.assertEquals(toAccountData.getBaseAmount().setScale(digits), toAccountLine.getConvertedAmount());

    }




    @Test
    public void shouldCreateLinearFxWithPredefinedConvertedAmount() {
        BigDecimal baseAmount = BigDecimal.ONE;
        BigDecimal converted = BigDecimal.valueOf(25);

        CurrencyCode quoteCurrency = CurrencyCode.CAD;
        LinearInternalTransferPipelineData data =
                linearBaseBuyForeignWithConvertedAmount(baseAmount, converted);
        GetAccountsInfoForTransactionResponse response =
                FxDataBuilder.baseBuyForeignAccountsInfo(quoteCurrency);
        FxRateGetLatestResponse latestConversion = DataBuilder.latestConversion();

        withFx(baseAmount, converted, converted, latestConversion,
                data, response);

    }


    @Test
    public void shouldCreateLinearFxBaseBuyForeign() {
        BigDecimal baseAmount = BigDecimal.ONE;
        FxDirection fxDirection = FxDirection.BID;

        CurrencyCode quoteCurrency = CurrencyCode.CAD;
        LinearInternalTransferPipelineData data =
                linearBaseBuyForeign(baseAmount, fxDirection);
        GetAccountsInfoForTransactionResponse response =
                FxDataBuilder.baseBuyForeignAccountsInfo(quoteCurrency);
        FxRateGetLatestResponse latestConversion = DataBuilder.latestConversion();

        BigDecimal expected = FxRateCurrencyConversion
                .V1.convertMultiply(baseAmount, latestConversion,
                        FxRateData.quote(quoteCurrency, fxDirection));

        withFx(baseAmount, null, expected, latestConversion,
                data, response);

    }

    @Test
    public void shouldCreateLinearFxForeignBuyBase() {
        BigDecimal foreignAmount = BigDecimal.valueOf(1000);
        FxDirection fxDirection = FxDirection.ASK;

        CurrencyCode quoteCurrency = CurrencyCode.CAD;
        LinearInternalTransferPipelineData data =
                linearForeignBuyBase(foreignAmount, fxDirection);
        GetAccountsInfoForTransactionResponse response =
                FxDataBuilder.foreignBuyBaseAccountsInfo(quoteCurrency);
        FxRateGetLatestResponse latestConversion = DataBuilder.latestConversion();

        BigDecimal expected = FxRateCurrencyConversion
                .V1.convertDivide(foreignAmount, latestConversion,
                        FxRateData.quote(quoteCurrency, fxDirection));

        withFx(foreignAmount, null, expected, latestConversion,
                data, response);

    }


    private void withFx(BigDecimal baseAmount,
                        BigDecimal converted,
                        BigDecimal expectedConverted,
                        FxRateGetLatestResponse latestConversion,
                        LinearInternalTransferPipelineData data,
                        GetAccountsInfoForTransactionResponse response) {


        int digits = 4;

        doReturn(Mono.just(response))
                .when(accountServiceController)
                .getAccountsInfoForTransaction(any(GetAccountsInfoForTransactionRequest.class));
        doReturn(Mono.just(latestConversion))
                .when(fxRateServiceController)
                .getLatestFxRate(any(FxRateGetLatestRequest.class));

        TransactionCreationPipelineResult result = internalTransferPipeline
                .customLinearTransfer(data).block();

        cleanRepo.transactionId = result.getTransactionId();
        cleanRepo.transactionLineIds = result.getTransactionLineIds();

        Map<String, TransactionLine> transactionLineMap = getTransactionLineMap(result.getTransactionId());
        Map<String, CustomAccountTransferData> customAccountTransferDataMap =
                getCustomAccountTransferDataMap(data);



        TransactionLine toAccountLine = transactionLineMap.get(toAccountId);
        var toAccountData = customAccountTransferDataMap.get(toAccountId);

        Assertions.assertEquals(expectedConverted.setScale(digits),
                toAccountLine.getConvertedAmount());

    }
}
