package com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register;

import com.panut.centralbanking.appcore.exceptions.SystemMisused;
import com.panut.centralbanking.transactionservice.api.service.account.AccountServiceController;
import com.panut.centralbanking.transactionservice.api.service.account.request.VerifyAccountsStateRequest;
import com.panut.centralbanking.transactionservice.api.service.fxRate.FxRateServiceController;
import com.panut.centralbanking.transactionservice.api.service.fxRate.request.FxRateGetLatestRequest;
import com.panut.centralbanking.transactionservice.application.conversion.FxRateCurrencyConversion;
import com.panut.centralbanking.transactionservice.application.ledgeraccount.query.LedgerAccountQuery;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.create.data.create.TransactionLineCreateData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.post.PostTransactionRegisterData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.AccountData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.PreTransactionRegisterData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.validate.TransactionValidationPipeline;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

@Component
public class TransactionRegisterPipeline {
    private final TransactionValidationPipeline transactionValidationPipeline;
    private final LedgerAccountQuery ledgerAccountQuery;
    private final FxRateServiceController fxRateServiceController;
    private final AccountServiceController accountServiceController;

    public TransactionRegisterPipeline(TransactionValidationPipeline transactionValidationPipeline,
                                       LedgerAccountQuery ledgerAccountQuery,
                                       FxRateServiceController fxRateServiceController,
                                       AccountServiceController accountServiceController) {
        this.transactionValidationPipeline = transactionValidationPipeline;
        this.ledgerAccountQuery = ledgerAccountQuery;
        this.fxRateServiceController = fxRateServiceController;
        this.accountServiceController = accountServiceController;
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
        return accountServiceController
                .verifyAccountStates(new VerifyAccountsStateRequest(registerData.getUsingAccountIds()))
                .then(ledgerAccountQuery.getLedgerAccountMapByCodes(registerData.getUsingLedgerCodes()))
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
