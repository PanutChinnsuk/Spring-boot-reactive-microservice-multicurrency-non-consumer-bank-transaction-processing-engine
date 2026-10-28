package com.panut.centralbanking.transactionservice.application.transactions.pipelines.business;

import com.panut.centralbanking.appcore.exceptions.SystemMisused;
import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.transactionservice.api.service.account.AccountServiceController;
import com.panut.centralbanking.transactionservice.api.service.account.response.GetAccountsInfoForTransactionResponse;
import com.panut.centralbanking.transactionservice.api.service.account.response.queryresult.AccountInfoForTransactionQueryResult;
import com.panut.centralbanking.transactionservice.api.service.fxRate.FxRateServiceController;
import com.panut.centralbanking.transactionservice.api.service.fxRate.request.FxRateGetLatestRequest;
import com.panut.centralbanking.transactionservice.api.service.fxRate.response.FxRateGetLatestResponse;
import com.panut.centralbanking.transactionservice.application.ledgeraccount.query.LedgerAccountQuery;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.data.ForeignBuyBaseTransferData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.repository.BusinessPipelineRepository;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.TransactionCreationPipelineResult;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.TransactionRegisterPipeline;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.FxRateData;
import com.panut.centralbanking.transactionservice.core.FxDirection;
import com.panut.centralbanking.transactionservice.core.LedgerType;
import com.panut.centralbanking.transactionservice.core.TransactionType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;

enum FxPipeline {
    FOREIGN_BUY_BASE,
    BASE_BUY_FOREIGN
}

class Chain {
    private FxRateGetLatestResponse fxRateGetLatestResponse;
    private Map<String,String> ledgerCodeMap;


    public Mono<Chain> fxRateGetLatestResponse(final FxRateGetLatestResponse fxRateGetLatestResponse) {
        return Mono.defer(() -> {
            this.fxRateGetLatestResponse = fxRateGetLatestResponse;

            return Mono.just(this);
        });
    }

    public Mono<Chain> ledgerCodeMap(final Map<String,String> ledgerCodeMap) {
        return Mono.defer(() -> {
            this.ledgerCodeMap = ledgerCodeMap;

            return Mono.just(this);
        });
    }
}

@Component
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

    protected Mono<FxRateGetLatestResponse> getFxRate(final FxRateData fxRateData) {
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

    protected Mono<GetAccountsInfoForTransactionResponse>
    defaultFxTransferChain(final ForeignBuyBaseTransferData data,
                           final GetAccountsInfoForTransactionResponse response,
                           final FxPipeline fxPipeline) {
        return Mono.fromCallable(() -> response.getAccount(data.getToAccountId()))
                .map(AccountInfoForTransactionQueryResult::getAccountCurrency)
                .flatMap(quoteCurrency -> toFxRateData(quoteCurrency, fxPipeline))
                .flatMap(this::getFxRate)
                .flatMap(fxRate -> new Chain().fxRateGetLatestResponse(fxRate))
                .flatMap(chain -> ledgerAccountQuery
                        .getLedgerAccountMapByCodes(data.getLedgerCodeSet(),
                                FIXED_EXPECTED_LEDGER_TYPES)
                        .flatMap(chain::ledgerCodeMap))
                .flatMap(chain -> {

                })
    }

    public Mono<GetAccountsInfoForTransactionResponse> foreignBuyBase(final ForeignBuyBaseTransferData data) {
        return repository.getAccountsInfoForTransaction(data)
                .map(response -> response.compact()
                        .expectedSize(data.getFixedSize())
                        .sort()
                        .expectedNonInternalSize(data.getFixedNonInternalSize())
                        .expectedInternalSize(data.getFixedInternalSize())
                        .expectedAllAccountStatusActive()
                        .expectedAllNonInternalNotBaseCurrency()
                        .expectedBaseCurrencyInternalAccounts())
                .flatMap(response -> defaultFxTransferChain(data,
                        response, FxPipeline.FOREIGN_BUY_BASE));


    }
}

