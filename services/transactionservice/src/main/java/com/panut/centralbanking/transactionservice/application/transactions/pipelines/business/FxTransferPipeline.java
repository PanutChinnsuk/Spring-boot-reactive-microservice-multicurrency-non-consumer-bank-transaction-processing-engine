package com.panut.centralbanking.transactionservice.application.transactions.pipelines.business;

import com.panut.centralbanking.appcore.exceptions.SystemMisused;
import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.transactionservice.api.service.account.AccountServiceController;
import com.panut.centralbanking.transactionservice.api.service.account.response.GetAccountsInfoForTransactionResponse;
import com.panut.centralbanking.transactionservice.api.service.account.response.queryresult.AccountInfoForTransactionQueryResult;
import com.panut.centralbanking.transactionservice.api.service.fxRate.FxRateServiceController;
import com.panut.centralbanking.transactionservice.api.service.fxRate.request.FxRateGetLatestRequest;
import com.panut.centralbanking.transactionservice.api.service.fxRate.response.FxRateGetLatestResponse;
import com.panut.centralbanking.transactionservice.application.calculations.conversions.FxRateCurrencyConversion;
import com.panut.centralbanking.transactionservice.application.calculations.profits.FxRateProfitCalculator;
import com.panut.centralbanking.transactionservice.application.ledgeraccount.query.LedgerAccountQuery;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.data.AbstractPipelineTransferData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.data.ForeignBuyBaseTransferData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.repository.BusinessPipelineRepository;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.TransactionCreationPipelineResult;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.TransactionRegisterPipeline;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.AccountData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.FxRateData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.PreTransactionRegisterData;
import com.panut.centralbanking.transactionservice.core.LedgerType;
import com.panut.centralbanking.transactionservice.core.TransactionType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

enum FxPipeline {
    FOREIGN_BUY_BASE,
    BASE_BUY_FOREIGN
}

@Getter
class FxTransferPipelineChain {
    private FxRateGetLatestResponse fxRateGetLatestResponse;
    private Map<String,String> ledgerCodeMap;
    private FxRateData fxRateData;
    private GetAccountsInfoForTransactionResponse getAccountsInfoForTransactionResponse;


    public Mono<FxTransferPipelineChain> fxRateGetLatestResponse(final FxRateGetLatestResponse fxRateGetLatestResponse) {
        return Mono.defer(() -> {
            this.fxRateGetLatestResponse = fxRateGetLatestResponse;

            return Mono.just(this);
        });
    }

    public Mono<FxTransferPipelineChain> ledgerCodeMap(final Map<String,String> ledgerCodeMap) {
        return Mono.defer(() -> {
            this.ledgerCodeMap = ledgerCodeMap;

            return Mono.just(this);
        });
    }

    public Mono<FxTransferPipelineChain> fxRateData(final FxRateData fxRateData) {
        return Mono.defer(() -> {
            this.fxRateData = fxRateData;

            return Mono.just(this);
        });
    }

    public Mono<FxTransferPipelineChain> accountsInfoForTransactionResponse
            (final GetAccountsInfoForTransactionResponse getAccountsInfoForTransactionResponse) {
        return Mono.defer(() -> {
            this.getAccountsInfoForTransactionResponse = getAccountsInfoForTransactionResponse;

            return Mono.just(this);
        });
    }
}

@Component
@Transactional(propagation = Propagation.REQUIRED)
@Slf4j
public class FxTransferPipeline {
    private final BusinessPipelineRepository repository;
    private final TransactionRegisterPipeline transactionRegisterPipeline;
    private final AccountServiceController accountServiceController;
    private final FxRateServiceController fxRateServiceController;
    private final LedgerAccountQuery ledgerAccountQuery;


    public FxTransferPipeline(BusinessPipelineRepository repository,
                              TransactionRegisterPipeline transactionRegisterPipeline,
                              AccountServiceController accountServiceController,
                              FxRateServiceController fxRateServiceController,
                              LedgerAccountQuery ledgerAccountQuery) {
        this.repository = repository;
        this.transactionRegisterPipeline = transactionRegisterPipeline;
        this.accountServiceController = accountServiceController;
        this.fxRateServiceController = fxRateServiceController;
        this.ledgerAccountQuery = ledgerAccountQuery;
    }

    //SELF REMINDER >>> CHANGE TO ACCEPT TRANSACTION TYPE AND GET EXPECTED LEDGER TYPE
    //ACCORDINGLY IF NEEDED MORE THAN JUST LIABILITY AND NORMAL FX_TRANSFER
    private final TransactionType FIXED_TRANSACTION_TYPE = TransactionType.FX_TRANSFER;
    private Set<LedgerType> FIXED_EXPECTED_LEDGER_TYPES =
            Set.of(LedgerType.LIABILITY);

    protected Mono<FxRateGetLatestResponse> getFxRateConversion(final FxRateData fxRateData) {
        return fxRateServiceController
                .getLatestFxRate(FxRateGetLatestRequest.withFxRateData(fxRateData));
    }

    protected Mono<FxRateData> toFxRateData(final CurrencyCode quoteCurrency,
                                            final FxPipeline fxPipeline) {
        return Mono.defer(() -> {
            return switch (fxPipeline) {
                case FOREIGN_BUY_BASE -> Mono.just(FxRateData.quoteAsk(quoteCurrency));
                case BASE_BUY_FOREIGN -> Mono.just(FxRateData.quoteBid(quoteCurrency));
                default -> Mono.error(() -> new SystemMisused("Invalid fxPipeline."));
            };

        });
    }

    private Mono<FxTransferPipelineChain> preTransfer(final String accountIdToGetQuoteCurrency,
                final AbstractPipelineTransferData data,
                final GetAccountsInfoForTransactionResponse response,
                final FxPipeline fxPipeline) {
        return Mono.fromCallable(() -> response.getAccount(accountIdToGetQuoteCurrency))
                .map(AccountInfoForTransactionQueryResult::getAccountCurrency)
                .flatMap(quoteCurrency -> toFxRateData(quoteCurrency, fxPipeline))
                .flatMap(fxRateData -> getFxRateConversion(fxRateData)
                        .flatMap(latestConversion ->
                                new FxTransferPipelineChain().fxRateGetLatestResponse(latestConversion))
                        .flatMap(chain ->
                                chain.fxRateData(fxRateData.withId(chain
                                        .getFxRateGetLatestResponse()
                                        .getFxRateId()))
                        )
                        .flatMap(chain ->
                                chain.accountsInfoForTransactionResponse(response)
                        )
                )
                .flatMap(chain -> ledgerAccountQuery
                        .getLedgerAccountMapByCodes(data.getLedgerCodeSet(),
                                FIXED_EXPECTED_LEDGER_TYPES)
                        .flatMap(chain::ledgerCodeMap));
    }

    protected Mono<TransactionCreationPipelineResult>
    defaultForeignBuyBase(final FxTransferPipelineChain fxTransferPipelineChain,
                          final ForeignBuyBaseTransferData data) {
        return Mono.just(fxTransferPipelineChain)
                .map(chain -> {
                    BigDecimal uncovertedForeignAmount = data.getForeignAmount();
                    FxRateGetLatestResponse latestConversion = chain.getFxRateGetLatestResponse();
                    FxRateData fxRateData = chain.getFxRateData();
                    GetAccountsInfoForTransactionResponse response = chain
                            .getGetAccountsInfoForTransactionResponse();
                    var fromAccount = response.getAccount(data.getFromAccountId());
                    var toAccount = response.getAccount(data.getToAccountId());
                    var internalProfitAccount = response.getAccount(data.getInternalFxProfitAccountId());
                    var internalFxClearing = response.getAccount(data.getInternalFxClearingAccountId());
                    String ledgerCode = data.getFxLedgerCode().getValue();
                    String ledgerAccountId = chain.getLedgerCodeMap().get(ledgerCode);

                    BigDecimal convertedForeignAmount = FxRateCurrencyConversion.V1
                            .convertDivide(uncovertedForeignAmount, latestConversion, fxRateData);
                    BigDecimal convertedProfitAmount = FxRateProfitCalculator.V1
                            .foreignBuyBaseProfit(uncovertedForeignAmount, latestConversion);

                    List<AccountData> accountDatas = List.of(
                            AccountData.V1.fxDebit(fromAccount,
                                    uncovertedForeignAmount,
                                    convertedForeignAmount,
                                    ledgerAccountId),
                            AccountData.V1.linearCredit(toAccount,
                                    convertedForeignAmount,
                                    ledgerAccountId),
                            AccountData.V1.linearCredit(internalProfitAccount,
                                    convertedProfitAmount,
                                    ledgerAccountId),
                            AccountData.V1.linearClearingDebit(internalFxClearing,
                                    convertedProfitAmount,
                                    ledgerAccountId)
                    );


                    return new PreTransactionRegisterData(FIXED_TRANSACTION_TYPE,
                            accountDatas, chain.getFxRateData());
                })
                .flatMap(transactionRegisterPipeline::register);
    }



    public Mono<TransactionCreationPipelineResult> foreignBuyBase(final ForeignBuyBaseTransferData data) {
        return repository.getAccountsInfoForTransaction(data)
                .doOnNext(a -> log.info(
                        "After calling account service from business pipeline repository"))
                .map(response -> response.compact()
                        .expectedSize(data.getFixedSize())
                        .sort()
                        .expectedNonInternalSize(data.getFixedNonInternalSize())
                        .expectedInternalSize(data.getFixedInternalSize())
                        .expectedAllAccountStatusActive()
                        .expectedAllNonInternalNotBaseCurrency()
                        .expectedBaseCurrencyInternalAccounts())
                .doOnNext(a -> log.info(
                        "Validated response info from account service"))
                .map(response -> {
                    response.getAccount(data.getFromAccountId()).notExpectedAccountCurrency(
                            CurrencyCode.getBaseCurrency());
                    response.getAccount(data.getToAccountId()).expectedAccountCurrency(
                            CurrencyCode.getBaseCurrency());

                    return response;
                })
                .doOnNext(a -> log.info(
                        "Validated pricipal accounts' currency"))
                .flatMap(response -> preTransfer(
                        data.getFromAccountId(),
                        data,
                        response,
                        FxPipeline.FOREIGN_BUY_BASE))
                .doOnNext(a -> log.info(
                        "Before passing to defaultForeignBuyBase"))
                .flatMap(chain -> defaultForeignBuyBase(chain, data));


    }
}

