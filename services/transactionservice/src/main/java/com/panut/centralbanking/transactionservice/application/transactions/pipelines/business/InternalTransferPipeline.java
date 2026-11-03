package com.panut.centralbanking.transactionservice.application.transactions.pipelines.business;

import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.transactionservice.api.service.account.response.GetAccountsInfoForTransactionResponse;
import com.panut.centralbanking.transactionservice.api.service.account.response.queryresult.AccountInfoForTransactionQueryResult;
import com.panut.centralbanking.transactionservice.application.ledgeraccount.query.LedgerAccountQuery;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.data.LinearInternalTransferPipelineData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.CustomAccountTransferDataPolicy;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.InternalTransferPipelinePolicies;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.v1.InternalLinearTransferPipelineModuleV1;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.v1.data.CustomAccountTransferData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.repository.BusinessPipelineRepository;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.TransactionCreationPipelineResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
@Transactional(propagation = Propagation.REQUIRED)
public class InternalTransferPipeline {
    private final LedgerAccountQuery ledgerAccountQuery;
    private final InternalLinearTransferPipelineModuleV1 v1Module;
    private final BusinessPipelineRepository repository;


    public InternalTransferPipeline(LedgerAccountQuery ledgerAccountQuery,
                                    InternalLinearTransferPipelineModuleV1 v1Module,
                                    BusinessPipelineRepository repository) {
        this.ledgerAccountQuery = ledgerAccountQuery;
        this.v1Module = v1Module;
        this.repository = repository;
    }



    public Mono<TransactionCreationPipelineResult> customLinearTransfer(
            final LinearInternalTransferPipelineData data) {
        return repository.getAccountsInfoForTransaction(data)
                .doOnNext(a -> log.info("After get account info for " +
                        "transaction."))
                .map(response ->
                        response.compact()
                                .expectedSize(data.getFixedSize())
                                .limitedQuoteCurrencySize(data.getMaximumQuoteCurrencySize())
                )
                .doOnNext(a -> log.info("Validated default requirement " +
                        "for accounts info response"))
                .map(response -> {
                    InternalTransferPipelinePolicies
                            .applyTransferPolicyValidation(response, data.getTransferPolicies());
                    InternalTransferPipelinePolicies
                            .applyPerAccountPolicyValidation(response, data.getCustomDatas());
                    return response;
                })
                .doOnNext(a -> log.info("Apply validation according " +
                        "to custom data validation policies"))
                .flatMap(response ->
                        v1Module.defaultLinearCustomTransfer(response, data))
                .doOnSuccess(a ->
                        log.info("Successfully created custom internal linear transaction."))
                .doOnError(error ->
                        log.error("Failed to create custom internal linear transaction, {}",
                                error.getLocalizedMessage()));
    }





}
