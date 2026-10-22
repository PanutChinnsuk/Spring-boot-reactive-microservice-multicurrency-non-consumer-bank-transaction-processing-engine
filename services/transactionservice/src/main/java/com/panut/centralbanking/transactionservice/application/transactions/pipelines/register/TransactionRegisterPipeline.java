package com.panut.centralbanking.transactionservice.application.transactions.pipelines.register;

import com.panut.centralbanking.appcore.exceptions.SystemMisused;
import com.panut.centralbanking.transactionservice.api.service.FxRateServiceController;
import com.panut.centralbanking.transactionservice.api.service.request.FxRateGetLatestRequest;
import com.panut.centralbanking.transactionservice.application.conversion.FxRateCurrencyConversion;
import com.panut.centralbanking.transactionservice.application.ledgeraccount.query.LedgerAccountQuery;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.create.data.create.TransactionLineCreateData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.register.data.post.PostTransactionRegisterData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.register.data.pre.AccountData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.register.data.pre.PreTransactionRegisterData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.validate.TransactionValidationPipeline;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TransactionRegisterPipeline {
    private final TransactionValidationPipeline transactionValidationPipeline;
    private final LedgerAccountQuery ledgerAccountQuery;
    private final FxRateServiceController fxRateServiceController;

    public TransactionRegisterPipeline(TransactionValidationPipeline transactionValidationPipeline,
                                       LedgerAccountQuery ledgerAccountQuery,
                                       FxRateServiceController fxRateServiceController) {
        this.transactionValidationPipeline = transactionValidationPipeline;
        this.ledgerAccountQuery = ledgerAccountQuery;
        this.fxRateServiceController = fxRateServiceController;
    }



    private BigDecimal getConvertedAmount(final PreTransactionRegisterData registerData,
                                          final RegisterPipelineChain chain,
                                          final AccountData accountData) {
        boolean isUsingFxRate = !registerData.getFxRateData().isEmpty();
        if (isUsingFxRate && !chain.hasFxRate()) {
            throw new SystemMisused("Transaction is required fx rate but register chain does not" +
                    " contain fx rate.");
        }

        return isUsingFxRate
                ? FxRateCurrencyConversion.convert(accountData.getAmount(),
                chain.getFxRateGetLatestResponse(),
                registerData.getFxRateData().getFxDirection())
                : accountData.getAmount();
    }

    public Mono<Void> register(final PreTransactionRegisterData registerData) {
        return ledgerAccountQuery.getLedgerAccountMapByCodes(registerData.getUsingLedgerCodes())
                .flatMap(ledgerAccounts -> {
                    return new RegisterPipelineChain().ledgerAccounts(ledgerAccounts);
                })
                .flatMap(chain -> {
                    if (registerData.getFxRateData().isEmpty())
                        return Mono.just(chain);

                    return fxRateServiceController
                            .getLatestFxRate(FxRateGetLatestRequest.withFxRateData(
                                    registerData.getFxRateData()
                            ))
                            .flatMap(fxRate ->
                                    chain.fxRateGetLatestResponse(fxRate));
                        })
                .flatMap(chain -> {
                    List<TransactionLineCreateData> lineDatas = registerData.getAccountDatas()
                            .stream()
                            .map(accountData -> new TransactionLineCreateData(
                                    accountData.getAccountId(),
                                    accountData.getDirection(),
                                    accountData.getAmount(),
                                    getConvertedAmount(registerData, chain, accountData),
                                    chain.getLedgerAccounts().get(accountData.getLedgerCode())
                            )).toList();

                    return chain.lineDatas(lineDatas);
                })
                .map(chain -> new PostTransactionRegisterData(
                        chain.getLineDatas())
                )
                .then();

    }
}
