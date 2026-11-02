package com.panut.centralbanking.transactionservice.application.transactions.pipelines.business;

import com.panut.centralbanking.appcore.exceptions.SystemMisused;
import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.transactionservice.api.service.account.response.GetAccountsInfoForTransactionResponse;
import com.panut.centralbanking.transactionservice.api.service.account.response.queryresult.AccountInfoForTransactionQueryResult;
import com.panut.centralbanking.transactionservice.api.service.fxRate.FxRateServiceController;
import com.panut.centralbanking.transactionservice.api.service.fxRate.request.FxRateGetLatestRequest;
import com.panut.centralbanking.transactionservice.api.service.fxRate.response.FxRateGetLatestResponse;
import com.panut.centralbanking.transactionservice.application.ledgeraccount.query.LedgerAccountQuery;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.data.AbstractPipelineTransferData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.data.BaseBuyForeignTransferData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.data.ForeignBuyBaseTransferData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.FxTransferPipeline.v1.FxPipeline;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.TransferPipeline.v1.TransferPipelineChain;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.FxTransferPipeline.v1.FxTransferPipelineModuleV1;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.repository.BusinessPipelineRepository;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.TransactionCreationPipelineResult;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.FxRateData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Component
@Transactional(propagation = Propagation.REQUIRED)
@Slf4j
public class FxTransferPipeline {
    private final BusinessPipelineRepository repository;
    private final FxRateServiceController fxRateServiceController;
    private final LedgerAccountQuery ledgerAccountQuery;
    private final FxTransferPipelineModuleV1 v1Module;


    public FxTransferPipeline(BusinessPipelineRepository repository,
                              FxRateServiceController fxRateServiceController,
                              LedgerAccountQuery ledgerAccountQuery,
                              FxTransferPipelineModuleV1 v1Module) {
        this.repository = repository;
        this.fxRateServiceController = fxRateServiceController;
        this.ledgerAccountQuery = ledgerAccountQuery;
        this.v1Module = v1Module;
    }



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

    private Mono<TransferPipelineChain> preTransfer(final String accountIdToGetQuoteCurrency,
                                                    final AbstractPipelineTransferData data,
                                                    final GetAccountsInfoForTransactionResponse response,
                                                    final FxPipeline fxPipeline) {
        return Mono.fromCallable(() -> response.getAccount(accountIdToGetQuoteCurrency))
                .doOnNext(a -> log.info("after getting current transaction " +
                        "quote currency for fx transfer"))
                .map(AccountInfoForTransactionQueryResult::getAccountCurrency)
                .doOnNext(a -> log.info("Mapped to just quote currency"))
                .flatMap(quoteCurrency -> toFxRateData(quoteCurrency, fxPipeline))
                .doOnNext(a -> log.info("quote currency to fx rate data"))
                .flatMap(fxRateData -> getFxRateConversion(fxRateData)
                        .doOnNext(a -> log.info("get latest fx " +
                                "conversion from fx rate service"))
                        .flatMap(latestConversion ->
                                new TransferPipelineChain().fxRateGetLatestResponse(latestConversion))
                        .doOnNext(a -> log.info("put latest fx rate conversion " +
                                "into current pipeline chain"))
                        .flatMap(chain ->
                                chain.fxRateData(fxRateData.withId(chain
                                        .getFxRateGetLatestResponse()
                                        .getFxRateId()))
                        )
                        .doOnNext(a -> log.info("put fx rate data from before " +
                                "into current pipeline chain"))
                        .flatMap(chain ->
                                chain.accountsInfoForTransactionResponse(response)
                        )
                        .doOnNext(a -> log.info("put accounts info for transaction " +
                                "into current pipeline chain"))
                )
                .doOnNext(a -> log.info("before query for ledger codes map"))
                .flatMap(chain -> ledgerAccountQuery
                        .getLedgerAccountMapByCodes(data.getLedgerCodeSet(),
                        v1Module.FIXED_EXPECTED_LEDGER_TYPES)
                        .doOnNext(a -> log.info("query for ledger codes map"))
                        .flatMap(chain::ledgerCodeMap))
                .doOnNext(a -> log.info("completed pre transfer for fx " +
                        "transfer pipeline"));
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
                .flatMap(chain -> v1Module.defaultForeignBuyBase(chain, data))
                .doOnSuccess(a -> log.info("Successfully created fx " +
                        "transaction from quote to base (ask)"))
                .doOnError(error ->
                        log.error("Failed to create fx transaction from quote to base, {}",
                        error.getLocalizedMessage()));


    }



    public Mono<TransactionCreationPipelineResult> baseBuyForeign(final BaseBuyForeignTransferData data) {
        return repository.getAccountsInfoForTransaction(data)
                .doOnNext(a -> log.info(
                        "After calling account service from business pipeline repository"))
                .map(response -> response.compact()
                        .expectedSize(data.getFixedSize())
                        .sort()
                        .expectedNonInternalSize(data.getNonInternalSize())
                        .expectedInternalSize(data.getInternalSize())
                        .expectedAllAccountStatusActive()
                        .expectedBaseCurrencyInternalAccounts()
                )
                .doOnNext(a -> log.info(
                        "Validated response info from account service"))
                .map(response -> {
                    response.getAccount(data.getFromAccountId()).expectedAccountCurrency(
                            CurrencyCode.getBaseCurrency());
                    response.getAccount(data.getToAccountId()).notExpectedAccountCurrency(
                            CurrencyCode.getBaseCurrency());

                    return response;
                })
                .doOnNext(a -> log.info(
                        "Validated pricipal accounts' currency"))
                .flatMap(response -> preTransfer(
                        data.getToAccountId(), data, response, FxPipeline.BASE_BUY_FOREIGN))
                .doOnNext(a -> log.info(
                        "Before passing to defaultBaseBuyForeign"))
                .flatMap(chain -> v1Module.defaultBaseBuyForeign(chain, data))
                .doOnSuccess(a -> log.info("Successfully created fx " +
                        "transaction from base to quote (bid)"))
                .doOnError(error ->
                        log.error("Failed to create fx transaction from base to quote, {}",
                                error.getLocalizedMessage()));
    }
}

