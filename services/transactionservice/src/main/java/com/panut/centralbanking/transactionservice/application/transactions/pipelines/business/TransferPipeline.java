package com.panut.centralbanking.transactionservice.application.transactions.pipelines.business;

import com.panut.centralbanking.appcore.exceptions.SystemMisused;
import com.panut.centralbanking.transactionservice.api.service.account.AccountServiceController;
import com.panut.centralbanking.transactionservice.api.service.account.response.GetAccountsInfoForTransactionResponse;
import com.panut.centralbanking.transactionservice.application.ledgeraccount.query.ExpectedLedgerTypes;
import com.panut.centralbanking.transactionservice.application.ledgeraccount.query.LedgerAccountQuery;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.data.PipelineTransferData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.TransferPipeline.v1.PreListAccountDatasInfo;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.TransferPipeline.v1.TransferPipelineModuleV1;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.repository.BusinessPipelineRepository;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.TransactionCreationPipelineResult;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.TransactionRegisterPipeline;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.AccountData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.FxRateData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.PreTransactionRegisterData;
import com.panut.centralbanking.transactionservice.core.LedgerType;
import com.panut.centralbanking.transactionservice.core.TransactionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;


@Slf4j
@Component
@Transactional(propagation = Propagation.REQUIRED)
public class TransferPipeline {
    private final BusinessPipelineRepository repository;
    private final TransactionRegisterPipeline transactionRegisterPipeline;
    private final TransferPipelineModuleV1 v1Module;

    public TransferPipeline(BusinessPipelineRepository repository,
                            TransactionRegisterPipeline transactionRegisterPipeline,
                            TransferPipelineModuleV1 v1Module) {
        this.repository = repository;
        this.transactionRegisterPipeline = transactionRegisterPipeline;
        this.v1Module = v1Module;
    }


    public Mono<TransactionCreationPipelineResult> transfer(final PipelineTransferData data,
                                                            final TransactionType transactionType) {
        return repository.getAccountsInfoForTransaction(data)
                .doOnNext(a -> log.info(
                        "After calling account service from business pipeline repository"))
                .map(response -> response
                        .compact()
                        .expectedSize(data.getFixedSize())
                        .expectedAllAccountStatusActive()
                        .expectedSameCurrency())
                .doOnNext(a -> log.info(
                        "Validated response info from account service"))
                .flatMap(response ->
                        v1Module.defaultTransferChain(response, data, transactionType))
                .doOnNext(a -> log.info(
                        "Before passing to transaction register pipeline"))
                .flatMap(transactionRegisterPipeline::register)
                .doOnSuccess(a -> log.info(
                        "Successfully create transaction."))
                .doOnError(error ->
                        log.error("Failed to create transaction, {}", error.getLocalizedMessage()));
    }
}
