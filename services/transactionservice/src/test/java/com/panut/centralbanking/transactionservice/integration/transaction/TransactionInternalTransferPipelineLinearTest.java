package com.panut.centralbanking.transactionservice.integration.transaction;

import com.panut.centralbanking.appcore.integrationtest.SeededDatabase;
import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.transactionservice.IntegrationDevTest;
import com.panut.centralbanking.transactionservice.api.service.account.AccountServiceController;
import com.panut.centralbanking.transactionservice.api.service.account.request.GetAccountsInfoForTransactionRequest;
import com.panut.centralbanking.transactionservice.api.service.account.response.GetAccountsInfoForTransactionResponse;
import com.panut.centralbanking.transactionservice.api.service.fxRate.FxRateServiceController;
import com.panut.centralbanking.transactionservice.api.service.fxRate.request.FxRateGetLatestRequest;
import com.panut.centralbanking.transactionservice.api.service.fxRate.request.FxRateMultiGetLatestRequest;
import com.panut.centralbanking.transactionservice.api.service.fxRate.response.FxRateGetResponse;
import com.panut.centralbanking.transactionservice.api.service.fxRate.response.FxRateMultiGetResponse;
import com.panut.centralbanking.transactionservice.application.calculations.conversions.FxRateCurrencyConversion;
import com.panut.centralbanking.transactionservice.application.ledgeraccount.LedgerCode;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.InternalTransferPipeline;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.data.InternalLinearTransferPipelineData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.policies.CustomTransferDataPolicy;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.CustomInternalAccountProfitContext;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.policies.CustomPerAccountFxTransferDataPolicy;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.policies.CustomPerAccountTransferDataPolicy;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.policies.ledger.CustomTransferLedgerPolicy;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.policies.ledger.TransferLedgerPolicyEnum;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.v1.data.CustomAccountTransferData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.v1.data.CustomAccountTransferDataFxContext;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.TransactionCreationPipelineResult;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.TransactionMultiFxContext;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.FxRateData;
import com.panut.centralbanking.transactionservice.core.FxDirection;
import com.panut.centralbanking.transactionservice.core.TransactionLineDirection;
import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionLine;
import com.panut.centralbanking.transactionservice.integration.transaction.cleanrepo.RepositoryCleanUp;
import com.panut.centralbanking.transactionservice.integration.transaction.utils.DataBuilder;
import com.panut.centralbanking.transactionservice.integration.transaction.utils.FxDataBuilder;
import com.panut.centralbanking.transactionservice.testrepository.TransactionLineTestRepository;
import com.panut.centralbanking.transactionservice.testrepository.TransactionMultiFxContentTestRepository;
import com.panut.centralbanking.transactionservice.testrepository.TransactionProfileTestRepository;
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
    private RepositoryCleanUp cleanRepo;

    @MockitoSpyBean
    private AccountServiceController accountServiceController;

    @MockitoSpyBean
    private FxRateServiceController fxRateServiceController;

    @Autowired
    private TransactionLineTestRepository transactionLineTestRepository;

    @Autowired
    private TransactionProfileTestRepository profileTestRepository;

    @Autowired
    private TransactionMultiFxContentTestRepository fxContentTestRepository;


    private String fromAccountId = DataBuilder.account1;
    private String toAccountId = DataBuilder.account2;
    private String internal1 = DataBuilder.internalAccount1;
    private String internal2 = DataBuilder.internalAccount2;

    private Set<CustomTransferDataPolicy> defaultTransferPolicy() {
        return Set.of(
                CustomTransferDataPolicy.ACTIVE_ACCOUNTS_ONLY
        );
    }

    private Set<CustomPerAccountTransferDataPolicy> defaultPerAccountolicy() {
        return Set.of(
                CustomPerAccountTransferDataPolicy.IS_BASE_CURRENCY
        );
    }

    private List<CustomTransferLedgerPolicy> fillDebitPolicy(String accountId) {
        return List.of(
                new CustomTransferLedgerPolicy(TransferLedgerPolicyEnum.FILL_CLEARING_DEBIT)
                        .withAccountId(accountId)
        );
    }

    private List<CustomTransferLedgerPolicy> fillDebitPolicyWithContinue(String accountId,
                                                                         String fromKey,
                                                                         String toKey,
                                                                         String toClearingKey,
                                                                         String toForeign,
                                                                         String toClearForeign) {
        return List.of(
                new CustomTransferLedgerPolicy(TransferLedgerPolicyEnum.FILL_CLEARING_DEBIT)
                        .withAccountId(accountId).order(0),
                new CustomTransferLedgerPolicy(TransferLedgerPolicyEnum.CONTINUE_TRANSACTION)
                        .continueTransaction(fromKey, toKey).order(1),
                new CustomTransferLedgerPolicy(TransferLedgerPolicyEnum.CONTINUE_TRANSACTION)
                        .continueTransaction(fromKey, toClearingKey).order(2),
                new CustomTransferLedgerPolicy(TransferLedgerPolicyEnum.CONTINUE_TRANSACTION)
                        .continueTransaction(fromKey, toForeign).order(3),
                new CustomTransferLedgerPolicy(TransferLedgerPolicyEnum.CONTINUE_TRANSACTION)
                        .continueTransaction(toForeign, toClearForeign).order(4)
        );

    }





    private CustomAccountTransferData customAccountTransferData(String accountId,
                                                                BigDecimal amount,
                                                                TransactionLineDirection direction,
                                                                String optionalUniqueIdentityKey) {
        return new CustomAccountTransferData(accountId,
                amount,
                null,
                LedgerCode.COMMON_TRANSFER,
                direction,
                optionalUniqueIdentityKey,
                null,
                null,
                null);
    }

    private CustomAccountTransferData customAccountTransferData(String accountId,
                                                                BigDecimal amount,
                                                                TransactionLineDirection direction) {
        return customAccountTransferData(accountId, amount,
                direction, null);
    }

    private CustomAccountTransferData customAccountTransferData(String accountId,
                                                                BigDecimal amount,
                                                                BigDecimal convertedAmount,
                                                                TransactionLineDirection direction,
                                                                String optionalUniqueIdentityKey) {
        return new CustomAccountTransferData(accountId,
                amount,
                convertedAmount,
                LedgerCode.COMMON_TRANSFER,
                direction,
                optionalUniqueIdentityKey,
                null,
                null,
                null);
    }

    private CustomAccountTransferData customAccountTransferData(String accountId,
                                                                BigDecimal amount,
                                                                BigDecimal convertedAmount,
                                                                TransactionLineDirection direction) {
        return customAccountTransferData(accountId, amount, convertedAmount, direction,
                null);
    }



    private InternalLinearTransferPipelineData linearAtoB() {
        List<CustomAccountTransferData> perAccounts = List.of(
                customAccountTransferData(fromAccountId, BigDecimal.ONE,
                        TransactionLineDirection.DEBIT)
                        .withPerAccountPolicies(defaultPerAccountolicy()),
                customAccountTransferData(toAccountId, BigDecimal.ONE,
                        TransactionLineDirection.CREDIT)
                        .withPerAccountPolicies(defaultPerAccountolicy())
        );

        return new InternalLinearTransferPipelineData(perAccounts,
                defaultTransferPolicy(),
                null);
    }

    private InternalLinearTransferPipelineData linearBaseBuyForeignWithConvertedAmount(BigDecimal baseAmount,
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

        return new InternalLinearTransferPipelineData(perAccounts,
                defaultTransferPolicy(),
                null);
    }

    private InternalLinearTransferPipelineData linearBaseBuyForeign(BigDecimal baseAmount,
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

        return new InternalLinearTransferPipelineData(perAccounts,
                defaultTransferPolicy(),
                null);
    }

    private InternalLinearTransferPipelineData linearForeignBuyBase(BigDecimal foreignAmount,
                                                                    FxDirection fxDirection) {
        List<CustomAccountTransferData> perAccounts = List.of(
                customAccountTransferData(fromAccountId, foreignAmount,
                        TransactionLineDirection.DEBIT)
                        .withFxContext(new CustomAccountTransferDataFxContext(
                                fxDirection,
                                CustomPerAccountFxTransferDataPolicy.FOREIGN_PRINCIPAL
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
                        TransactionLineDirection.CLEARING_CREDIT),
                customAccountTransferData(internal2, BigDecimal.ZERO,
                        TransactionLineDirection.CLEARING_DEBIT)

        );



        return new InternalLinearTransferPipelineData(perAccounts,
                defaultTransferPolicy(),
                fillDebitPolicy(internal2));
    }


    private InternalLinearTransferPipelineData linearMultiForeignBuyBase(BigDecimal foreignAmount1,
                                                                         BigDecimal foreignAmount2,
                                                                         BigDecimal foreignAmount3,
                                                                    FxDirection fxDirection) {
        var totalForeign = foreignAmount1.add(foreignAmount2)
                .add(foreignAmount3);

        String foreign1 = DataBuilder.account1;
        String foreign2 = DataBuilder.account2;
        String foreign3 = DataBuilder.emptyAccount1;
        String foreignTo = DataBuilder.emptyAccount2;

        //middle man included
        String fromAccountKey = "cont1";
        String targetAccountKey = "cont2";
        String targetClearingAccountKey = "cont3";
        String targetToForeignAccountKey = "cont4";
        String targetClearForeignAccountKey = "cont5";


        List<CustomAccountTransferData> perAccounts = List.of(
                customAccountTransferData(foreign1, foreignAmount1,
                        TransactionLineDirection.DEBIT)
                        .withFxContext(new CustomAccountTransferDataFxContext(
                                fxDirection,
                                CustomPerAccountFxTransferDataPolicy.FOREIGN_PRINCIPAL
                        )),

                customAccountTransferData(foreign2, foreignAmount2,
                        TransactionLineDirection.DEBIT)
                        .withFxContext(new CustomAccountTransferDataFxContext(
                                fxDirection,
                                CustomPerAccountFxTransferDataPolicy.FOREIGN_PRINCIPAL
                        )),

                customAccountTransferData(foreign3, foreignAmount3,
                        TransactionLineDirection.DEBIT)
                        .withFxContext(new CustomAccountTransferDataFxContext(
                                fxDirection,
                                CustomPerAccountFxTransferDataPolicy.FOREIGN_PRINCIPAL
                        )),
                customAccountTransferData(foreignTo, foreignAmount1,
                        TransactionLineDirection.CREDIT)
                        .withFxContext(new CustomAccountTransferDataFxContext(
                                fxDirection,
                                CustomPerAccountFxTransferDataPolicy.FOREIGN_BUY_BASE,
                                foreign1
                        )),
                customAccountTransferData(foreignTo, foreignAmount2,
                        TransactionLineDirection.CREDIT)
                        .withFxContext(new CustomAccountTransferDataFxContext(
                                fxDirection,
                                CustomPerAccountFxTransferDataPolicy.FOREIGN_BUY_BASE,
                                foreign2
                        )),
                customAccountTransferData(foreignTo, foreignAmount3,
                        TransactionLineDirection.CREDIT, fromAccountKey)
                        .withFxContext(new CustomAccountTransferDataFxContext(
                                fxDirection,
                                CustomPerAccountFxTransferDataPolicy.FOREIGN_BUY_BASE,
                                foreign3
                        )),

                customAccountTransferData(internal1, foreignAmount1,
                        TransactionLineDirection.CREDIT)
                        .withFxContext(new CustomAccountTransferDataFxContext(
                                fxDirection,
                                CustomPerAccountFxTransferDataPolicy.FOREIGN_BUY_BASE,
                                foreign1
                        ))
                        .withProfitContext(new
                                CustomInternalAccountProfitContext(foreignAmount1)),

                customAccountTransferData(internal1, foreignAmount2,
                        TransactionLineDirection.CREDIT)
                        .withFxContext(new CustomAccountTransferDataFxContext(
                                fxDirection,
                                CustomPerAccountFxTransferDataPolicy.FOREIGN_BUY_BASE,
                                foreign2
                        ))
                        .withProfitContext(new
                                CustomInternalAccountProfitContext(foreignAmount1)),

                customAccountTransferData(internal1, foreignAmount3,
                        TransactionLineDirection.CREDIT)
                        .withFxContext(new CustomAccountTransferDataFxContext(
                                fxDirection,
                                CustomPerAccountFxTransferDataPolicy.FOREIGN_BUY_BASE,
                                foreign3
                        ))
                        .withProfitContext(new
                                CustomInternalAccountProfitContext(foreignAmount1)),

                customAccountTransferData(internal2, totalForeign,
                        TransactionLineDirection.CLEARING_CREDIT),
                customAccountTransferData(internal2, BigDecimal.ZERO,
                        TransactionLineDirection.CLEARING_DEBIT),

                /// middle man passing
                customAccountTransferData(internal1, BigDecimal.ZERO,
                        TransactionLineDirection.CREDIT, targetAccountKey),

                customAccountTransferData(internal1, BigDecimal.ZERO,
                        TransactionLineDirection.CLEARING_DEBIT, targetClearingAccountKey),

                /// parsed to another fx
                customAccountTransferData(foreign3, BigDecimal.ZERO,
                        TransactionLineDirection.CREDIT, targetToForeignAccountKey)
                        .withFxContext(new CustomAccountTransferDataFxContext(
                                FxDirection.BID,
                                CustomPerAccountFxTransferDataPolicy.BASE_BUY_FOREIGN
                        )),
                customAccountTransferData(internal1, BigDecimal.ZERO,
                        TransactionLineDirection.CLEARING_DEBIT,
                        targetClearForeignAccountKey)

        );



        return new InternalLinearTransferPipelineData(perAccounts,
                defaultTransferPolicy(),
                fillDebitPolicyWithContinue(internal2,
                        fromAccountKey, targetAccountKey, targetClearingAccountKey,
                        targetToForeignAccountKey, targetClearForeignAccountKey));
    }

    private InternalLinearTransferPipelineData linearMultiBaseBuyForeign(BigDecimal baseAmount1,
                                                                         BigDecimal baseAmount2,
                                                                         BigDecimal baseAmount3,
                                                                         FxDirection fxDirection) {
        var totalBase = baseAmount1.add(baseAmount2)
                .add(baseAmount3);

        String base1 = DataBuilder.account1;
        String base2 = DataBuilder.account2;
        String base3 = DataBuilder.emptyAccount1;
        String base1To = DataBuilder.emptyAccount2;
        String base2To = DataBuilder.emptyAccount3;
        String base3To = DataBuilder.emptyAccount4;



        List<CustomAccountTransferData> perAccounts = List.of(
                customAccountTransferData(base1, baseAmount1,
                        TransactionLineDirection.DEBIT)
                        .withPerAccountPolicies(defaultPerAccountolicy()),
                customAccountTransferData(base2, baseAmount2,
                        TransactionLineDirection.DEBIT)
                        .withPerAccountPolicies(defaultPerAccountolicy()),
                customAccountTransferData(base3, baseAmount3,
                        TransactionLineDirection.DEBIT)
                        .withPerAccountPolicies(defaultPerAccountolicy()),

                customAccountTransferData(base1To, baseAmount1,
                        TransactionLineDirection.CREDIT)
                        .withFxContext(new CustomAccountTransferDataFxContext(
                                fxDirection,
                                CustomPerAccountFxTransferDataPolicy.BASE_BUY_FOREIGN,
                                base1
                        )),

                customAccountTransferData(base2To, baseAmount2,
                        TransactionLineDirection.CREDIT)
                        .withFxContext(new CustomAccountTransferDataFxContext(
                                fxDirection,
                                CustomPerAccountFxTransferDataPolicy.BASE_BUY_FOREIGN,
                                base2
                        )),

                customAccountTransferData(base3To, baseAmount3,
                        TransactionLineDirection.CREDIT)
                        .withFxContext(new CustomAccountTransferDataFxContext(
                                fxDirection,
                                CustomPerAccountFxTransferDataPolicy.BASE_BUY_FOREIGN,
                                base3
                        )),

                customAccountTransferData(internal1, baseAmount1,
                        TransactionLineDirection.CLEARING_DEBIT)
                        .withFxContext(new CustomAccountTransferDataFxContext(
                                fxDirection,
                                CustomPerAccountFxTransferDataPolicy.BASE_BUY_FOREIGN,
                                base1To
                        )),
                customAccountTransferData(internal1, baseAmount2,
                        TransactionLineDirection.CLEARING_DEBIT)
                        .withFxContext(new CustomAccountTransferDataFxContext(
                                fxDirection,
                                CustomPerAccountFxTransferDataPolicy.BASE_BUY_FOREIGN,
                                base2To
                        )),
                customAccountTransferData(internal1, baseAmount3,
                        TransactionLineDirection.CLEARING_DEBIT)
                        .withFxContext(new CustomAccountTransferDataFxContext(
                                fxDirection,
                                CustomPerAccountFxTransferDataPolicy.BASE_BUY_FOREIGN,
                                base3To
                        )),


                customAccountTransferData(internal2, totalBase,
                        TransactionLineDirection.CLEARING_CREDIT)
                        .withPerAccountPolicies(defaultPerAccountolicy())
        );



        return new InternalLinearTransferPipelineData(perAccounts,
                defaultTransferPolicy(),
                null);
    }


    private Map<String, TransactionLine> getTransactionLineMap(String transactionId) {
        return transactionLineTestRepository
                .findByTransactionId(transactionId)
                .collectList().block()
                .stream()
                .collect(Collectors.toMap(
                        a -> a.getAccountId(),
                        Function.identity(),
                        (a,b) -> b
                ));
    }


    private Map<String, CustomAccountTransferData> getCustomAccountTransferDataMap
            (InternalLinearTransferPipelineData data) {
        return data.getCustomDatas()
                .stream()
                .collect(Collectors.toMap(
                        a -> a.getAccountId(),
                        Function.identity(),
                        (a,b) -> b
                ));
    }

    @AfterEach
    public void cleanUp() {
        cleanRepo.cleanUp();
    }

    @Test
    public void shouldCreateLinearNonFx() {
        InternalLinearTransferPipelineData data = linearAtoB();
        GetAccountsInfoForTransactionResponse response =
                DataBuilder.validAccountsInfo();
        int digits = 4;

        doReturn(Mono.just(response))
                .when(accountServiceController)
                .getAccountsInfoForTransaction(any(GetAccountsInfoForTransactionRequest.class));

        TransactionCreationPipelineResult result = internalTransferPipeline
                .customLinearTransfer(data).block();

        cleanRepo.transactionIds.add(result.getTransactionId());
        cleanRepo.transactionLineIds.addAll(result.getTransactionLineIds());

        Map<String, TransactionLine> transactionLineMap = getTransactionLineMap(result.getTransactionId());
        Map<String, CustomAccountTransferData> customAccountTransferDataMap =
                getCustomAccountTransferDataMap(data);


        TransactionLine fromAccountLine = transactionLineMap.get(fromAccountId);
        TransactionLine toAccountLine = transactionLineMap.get(toAccountId);
        var fromAccountData = customAccountTransferDataMap.get(fromAccountId);
        var toAccountData = customAccountTransferDataMap.get(toAccountId);

        Assertions.assertEquals(fromAccountData.getBaseAmount().setScale(digits), fromAccountLine.getConvertedAmount());
        Assertions.assertEquals(toAccountData.getBaseAmount().setScale(digits), toAccountLine.getConvertedAmount());

        validateProfileDirection(result.getTransactionId(),
                FxDirection.NONE);
    }




    @Test
    public void shouldCreateLinearFxWithPredefinedConvertedAmount() {
        BigDecimal baseAmount = BigDecimal.ONE;
        BigDecimal preDefinedConverted = BigDecimal.valueOf(25);

        CurrencyCode quoteCurrency = CurrencyCode.CAD;
        InternalLinearTransferPipelineData data =
                linearBaseBuyForeignWithConvertedAmount(baseAmount, preDefinedConverted);
        GetAccountsInfoForTransactionResponse response =
                FxDataBuilder.baseBuyForeignAccountsInfo(quoteCurrency);
        var latestConversions = DataBuilder.singleMultiGetLatestConversions(quoteCurrency);

        withFx(preDefinedConverted, preDefinedConverted, latestConversions,
                data, response);

    }


    @Test
    public void shouldCreateLinearFxBaseBuyForeign() {
        BigDecimal baseAmount = BigDecimal.ONE;
        FxDirection fxDirection = FxDirection.BID;

        CurrencyCode quoteCurrency = CurrencyCode.CAD;
        InternalLinearTransferPipelineData data =
                linearBaseBuyForeign(baseAmount, fxDirection);
        GetAccountsInfoForTransactionResponse response =
                FxDataBuilder.baseBuyForeignAccountsInfo(quoteCurrency);
        var latestConversions = DataBuilder.singleMultiGetLatestConversions(quoteCurrency);

        BigDecimal expected = FxRateCurrencyConversion
                .V1.convertMultiply(baseAmount, getFirst(latestConversions.getFxRateMap()),
                        FxRateData.quote(quoteCurrency, fxDirection));


        withFx(null, expected, latestConversions,
                data, response);

    }

    @Test
    public void shouldCreateLinearFxForeignBuyBase() {
        BigDecimal foreignAmount = BigDecimal.valueOf(1000);
        FxDirection fxDirection = FxDirection.ASK;

        CurrencyCode quoteCurrency = CurrencyCode.CAD;
        InternalLinearTransferPipelineData data =
                linearForeignBuyBase(foreignAmount, fxDirection);
        GetAccountsInfoForTransactionResponse response =
                FxDataBuilder.foreignBuyBaseAccountsInfo(quoteCurrency);
        var latestConversions = DataBuilder.singleMultiGetLatestConversions(quoteCurrency);

        BigDecimal expected = FxRateCurrencyConversion
                .V1.convertDivide(foreignAmount, getFirst(latestConversions.getFxRateMap()),
                        FxRateData.quote(quoteCurrency, fxDirection));

        withFx(null, expected, latestConversions,
                data, response);

    }

    @Test
    public void shouldCreateBaseBuyForeignMutliFx() {
        BigDecimal base1 = BigDecimal.valueOf(1000);
        BigDecimal base2 = BigDecimal.valueOf(1000);
        BigDecimal base3 = BigDecimal.valueOf(1000);
        FxDirection fxDirection = FxDirection.ASK;

        CurrencyCode quote1 = CurrencyCode.CAD;
        CurrencyCode quote2 = CurrencyCode.THB;
        CurrencyCode quote3 = CurrencyCode.AED;


        InternalLinearTransferPipelineData data =
                linearMultiBaseBuyForeign(base1,
                        base2,
                        base3, fxDirection);

        GetAccountsInfoForTransactionResponse response =
                FxDataBuilder.multiBaseBuyForeignAccountsInfo(quote1, quote2, quote3);
        var latestConversions = DataBuilder.multiGetLatestConversions(quote1, quote2,
                quote3);


        withFx(null, null, latestConversions,
                data, response);
    }

    @Test
    public void shouldCreateForeignBuyBaseMultiFx() {
        BigDecimal foreignAmount1 = BigDecimal.valueOf(257.25);
        BigDecimal foreignAmount2 = BigDecimal.valueOf(14.02);
        BigDecimal foreignAmount3 = BigDecimal.valueOf(2.262);
        FxDirection fxDirection = FxDirection.ASK;

        CurrencyCode quote1 = CurrencyCode.CAD;
        CurrencyCode quote2 = CurrencyCode.THB;
        CurrencyCode quote3 = CurrencyCode.AED;

        InternalLinearTransferPipelineData data =
                linearMultiForeignBuyBase(foreignAmount1,
                        foreignAmount2,
                        foreignAmount3, fxDirection);

        GetAccountsInfoForTransactionResponse response =
                FxDataBuilder.multiForeignBuyBaseAccountsInfo(quote1, quote2, quote3);
        var latestConversions = DataBuilder.multiGetLatestConversions(quote1, quote2,
                quote3);


        withFx( null, null, latestConversions,
                data, response);

    }

    private FxRateGetResponse getFirst(Map<CurrencyCode, FxRateGetResponse> map) {
        return map.values().stream().findFirst()
                .get();
    }


    private void withFx(BigDecimal preDefinedConverted,
                        BigDecimal expectedConverted,
                        FxRateMultiGetResponse fxRateMultiGetResponse,
                        InternalLinearTransferPipelineData data,
                        GetAccountsInfoForTransactionResponse response) {


        int digits = 4;

        doReturn(Mono.just(response))
                .when(accountServiceController)
                .getAccountsInfoForTransaction(any(GetAccountsInfoForTransactionRequest.class));
        doReturn(Mono.just(fxRateMultiGetResponse))
                .when(fxRateServiceController)
                .multiGetLatestFxRate(any(FxRateMultiGetLatestRequest.class));

        TransactionCreationPipelineResult result = internalTransferPipeline
                .customLinearTransfer(data).block();

        cleanRepo.transactionIds.add(result.getTransactionId());
        cleanRepo.transactionLineIds.addAll(result.getTransactionLineIds());

        Map<String, TransactionLine> transactionLineMap =
                getTransactionLineMap(result.getTransactionId());
        Map<String, CustomAccountTransferData> customAccountTransferDataMap =
                getCustomAccountTransferDataMap(data);


        transactionLineMap.forEach((k,v) -> {
            Assertions.assertTrue(v.getBasedAmount().signum() > 0);
            Assertions.assertTrue(v.getConvertedAmount().signum() > 0);
        });


        if (expectedConverted != null) {
            TransactionLine toAccountLine = transactionLineMap.get(toAccountId);
            Assertions.assertEquals(expectedConverted.setScale(digits),
                    toAccountLine.getConvertedAmount());

        }

        if (preDefinedConverted != null)
            validateProfileDirection(result.getTransactionId(),
                FxDirection.NONE);

        if (preDefinedConverted == null) {
            validateProfileDirection(result.getTransactionId(),
                    FxDirection.MULTI);

            var contents = fxContentTestRepository
                    .findByTransactionId(result.getTransactionId()).collectList().block();

            Assertions.assertTrue(!contents.isEmpty());
        }


    }

    private void validateProfileDirection(String transactionId,
                                          FxDirection fxDirection) {
        var profile = profileTestRepository.findById(transactionId).block();

        Assertions.assertEquals(fxDirection, profile.getFxDirection());
    }
}
