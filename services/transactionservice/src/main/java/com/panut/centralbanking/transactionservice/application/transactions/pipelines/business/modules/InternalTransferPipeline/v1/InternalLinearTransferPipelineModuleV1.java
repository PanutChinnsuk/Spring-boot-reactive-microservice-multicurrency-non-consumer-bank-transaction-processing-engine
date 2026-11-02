package com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.v1;

import com.panut.centralbanking.appcore.exceptions.SystemMisused;
import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.transactionservice.api.service.account.AccountServiceController;
import com.panut.centralbanking.transactionservice.api.service.account.response.GetAccountsInfoForTransactionResponse;
import com.panut.centralbanking.transactionservice.api.service.account.response.queryresult.AccountInfoForTransactionQueryResult;
import com.panut.centralbanking.transactionservice.api.service.fxRate.FxRateServiceController;
import com.panut.centralbanking.transactionservice.api.service.fxRate.request.FxRateGetLatestRequest;
import com.panut.centralbanking.transactionservice.api.service.fxRate.response.FxRateGetLatestResponse;
import com.panut.centralbanking.transactionservice.application.calculations.conversions.FxRateCurrencyConversion;
import com.panut.centralbanking.transactionservice.application.ledgeraccount.query.ExpectedLedgerTypes;
import com.panut.centralbanking.transactionservice.application.ledgeraccount.query.LedgerAccountQuery;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.data.LinearInternalTransferPipelineData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.TransferPipeline.v1.TransferPipelineChain;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.CustomPerAccountFxTransferDataPolicy;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.v1.data.CustomAccountTransferData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.repository.BusinessPipelineRepository;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.TransactionCreationPipelineResult;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.TransactionRegisterPipeline;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.AccountData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.FxRateData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.PreTransactionRegisterData;
import com.panut.centralbanking.transactionservice.core.FxDirection;
import com.panut.centralbanking.transactionservice.core.TransactionLineDirection;
import com.panut.centralbanking.transactionservice.core.TransactionType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class InternalLinearTransferPipelineModuleV1 {
    private final BusinessPipelineRepository repository;
    private final TransactionRegisterPipeline transactionRegisterPipeline;
    private final AccountServiceController accountServiceController;
    private final LedgerAccountQuery ledgerAccountQuery;
    private final FxRateServiceController fxRateServiceController;


    public InternalLinearTransferPipelineModuleV1(BusinessPipelineRepository repository,
                                                  TransactionRegisterPipeline transactionRegisterPipeline,
                                                  AccountServiceController accountServiceController,
                                                  LedgerAccountQuery ledgerAccountQuery,
                                                  FxRateServiceController fxRateServiceController) {
        this.repository = repository;
        this.transactionRegisterPipeline = transactionRegisterPipeline;
        this.accountServiceController = accountServiceController;
        this.ledgerAccountQuery = ledgerAccountQuery;
        this.fxRateServiceController = fxRateServiceController;
    }



    private final TransactionType FIXED_TRANSACTION_TYPE = TransactionType.INTERNAL;

    private FxRateData toFxRateData(final CurrencyCode quoteCurrency,
                                    final FxDirection fxDirection) {
        return FxRateData.quote(quoteCurrency, fxDirection);
    }

    private BigDecimal decideFxConversion(final CustomPerAccountFxTransferDataPolicy policy,
                                          final BigDecimal baseAmount,
                                          final FxRateData fxRateData,
                                          final FxRateGetLatestResponse latestConversion) {
        if (policy.FOREIGN_BUY_BASE())
            return FxRateCurrencyConversion
                    .V1.convertDivide(baseAmount, latestConversion,
                            fxRateData);

        if (policy.BASE_BUY_FOREIGN())
            return FxRateCurrencyConversion
                    .V1.convertMultiply(baseAmount, latestConversion,
                            fxRateData);


        throw new SystemMisused("Invalid CustomPerAccountFxTransferDataPolicy.");
    }

    private AccountData handlePerAccount(final CustomAccountTransferData data,
                                         final AccountInfoForTransactionQueryResult account,
                                         final Map<String,String> ledgerCodeMap,
                                         final FxRateGetLatestResponse latestConversion) {
        BigDecimal baseAmount = data.getBaseAmount();
        CurrencyCode accountCurrency = account.getAccountCurrency();
        String ledgerAccountId = ledgerCodeMap.get(data.getLedgerCode().getValue());
        TransactionLineDirection direction = data.getDirection();

        if (account.isBaseCurrency())
            return AccountData.V1.accountData(account,
                    baseAmount,
                    baseAmount,
                    ledgerAccountId,
                    direction);

        if (data.isCustomizedCovertedAmount()) {
            return AccountData.V1.accountData(account,
                    baseAmount,
                    data.getCustomConvertedAmount(),
                    ledgerAccountId,
                    direction);
        }

        if (latestConversion == null)
            throw new SystemMisused(
                    "Custom internal transfer data required latest fx rate conversion but is null.");

        var fxContext = data.getFxContext();
        FxDirection directionForFx = data.expectedNotNoneFxDirection()
                .getFxContext().getDirectionForFx();

        FxRateData fxRateData = toFxRateData(account.getAccountCurrency(),
                directionForFx);

        BigDecimal convertedAmount = decideFxConversion(fxContext.getPolicy(),
                data.getBaseAmount(), fxRateData, latestConversion);

        return AccountData.V1.accountData(account,
                baseAmount,
                convertedAmount,
                ledgerAccountId,
                direction);
    }

    public Mono<TransactionCreationPipelineResult>
    defaultLinearCustomTransfer(final GetAccountsInfoForTransactionResponse getAccountsInfoForTransactionResponse,
                                final LinearInternalTransferPipelineData pipelineData) {
        return Mono.just(getAccountsInfoForTransactionResponse)
                .flatMap(response -> ledgerAccountQuery
                        .getLedgerAccountMapByCodes(pipelineData.getLedgerCodes(),
                                ExpectedLedgerTypes.ALL())
                        .flatMap(ledgerCodeMap ->
                                new TransferPipelineChain()
                                        .ledgerCodeMap(ledgerCodeMap))
                        .flatMap(chain ->
                                chain.accountsInfoForTransactionResponse(response))
                        .flatMap(chain -> {
                            if (response.requiredFxRate()) {
                                List<CurrencyCode> quoteCurrencies = response.getQuoteCurrencies();
                                if (quoteCurrencies.size() != pipelineData.getMaximumQuoteCurrencySize())
                                    throw new SystemMisused("LinearCustomerTransferModuleV1 REQUIRED FX RATE, " +
                                            " only allowed maximum and minimum of 1 quote currency " +
                                            "for fx conversion.");

                                CurrencyCode quoteCurrency = quoteCurrencies
                                        .getFirst();

                                return fxRateServiceController.getLatestFxRate(
                                                FxRateGetLatestRequest.withDefault(quoteCurrency))
                                        .flatMap(latestConversion ->
                                                chain.fxRateGetLatestResponse(latestConversion));
                            }

                            return Mono.just(chain);
                        })

                )
                .map(chain -> {
                    var response = chain.getGetAccountsInfoForTransactionResponse();
                    List<CustomAccountTransferData> perAccountDatas = pipelineData
                            .getCustomDatas();

                    List<AccountData> accountDatas = new ArrayList<>();


                    perAccountDatas.forEach(data -> {
                        var account = response.getAccount(data.getAccountId());
                        AccountData accountData = handlePerAccount(data, account,
                                chain.getLedgerCodeMap(), chain.getFxRateGetLatestResponse());

                        accountDatas.add(accountData);
                    });

                    return new PreTransactionRegisterData(FIXED_TRANSACTION_TYPE,
                            accountDatas, FxRateData.empty());
                })
                .flatMap(transactionRegisterPipeline::register);
    }
}
