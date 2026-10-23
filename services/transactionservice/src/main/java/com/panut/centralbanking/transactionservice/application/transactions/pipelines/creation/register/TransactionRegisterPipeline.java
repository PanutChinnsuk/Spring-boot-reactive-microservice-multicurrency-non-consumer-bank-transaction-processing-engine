package com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register;

import com.panut.centralbanking.transactionservice.api.service.account.AccountServiceController;
import com.panut.centralbanking.transactionservice.api.service.fxRate.FxRateServiceController;
import com.panut.centralbanking.transactionservice.application.ledgeraccount.query.LedgerAccountQuery;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.TransactionCreationPipelineResult;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.create.data.TransactionLineCreateData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.post.PostTransactionRegisterData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.PreTransactionRegisterData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.validate.TransactionValidationPipeline;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
@Transactional(propagation = Propagation.REQUIRED)
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





    public Mono<TransactionCreationPipelineResult> register(final PreTransactionRegisterData registerData) {
        return Mono.defer(() -> {
                    List<TransactionLineCreateData> lineDatas = registerData.getAccountDatas()
                            .stream()
                            .map(accountData -> new TransactionLineCreateData(
                                    accountData.getAccountId(),
                                    accountData.getDirection(),
                                    accountData.getAmount(),
                                    accountData.getConvertedAmount(),
                                    accountData.getLedgerAccountId())
                            ).toList();
                    return new RegisterPipelineChain().lineDatas(lineDatas);
                })
                .doOnNext(a -> log.info("Start register mono chain, mapped account datas into transaction " +
                        "line create datas."))
                .map(chain -> new PostTransactionRegisterData(
                        registerData.getTransactionType(),
                        chain.getLineDatas(),
                        registerData.getFxRateData().getFxRateId(),
                        registerData.getFxRateData().getFxDirection(),
                        registerData.getUsingAccountIds())
                )
                .doOnNext(a -> log.info("RegisterPipelineChain to " +
                        "PostTransactionRegisterData"))
                .flatMap(post -> transactionValidationPipeline
                        .validate(post))
                .doOnSuccess(a -> log.info("Successfully registered" +
                        " transaction."))
                .doOnError(error -> log.error("Failed to register transaction, {}",
                        error.getLocalizedMessage()));

    }
}
