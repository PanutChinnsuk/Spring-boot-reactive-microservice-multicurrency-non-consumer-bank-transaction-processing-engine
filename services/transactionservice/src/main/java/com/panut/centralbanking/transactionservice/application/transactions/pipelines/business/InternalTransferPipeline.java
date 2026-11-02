package com.panut.centralbanking.transactionservice.application.transactions.pipelines.business;

import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.transactionservice.api.service.account.response.GetAccountsInfoForTransactionResponse;
import com.panut.centralbanking.transactionservice.api.service.account.response.queryresult.AccountInfoForTransactionQueryResult;
import com.panut.centralbanking.transactionservice.application.ledgeraccount.query.LedgerAccountQuery;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.data.LinearInternalTransferPipelineData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.CustomAccountTransferDataPolicy;
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

    private void applyTransferPolicyValidation(final GetAccountsInfoForTransactionResponse response,
                                       final List<CustomAccountTransferDataPolicy> policies) {
        if (policies.isEmpty()) return;

        policies.forEach(policy -> {
            if (policy.ACTIVE_ACCOUNTS_ONLY())
                response.expectedAllAccountStatusActive();
        });

    }

    private void applyPerAccountPolicyValidation(final GetAccountsInfoForTransactionResponse response,
                                                 final List<CustomAccountTransferData> perAccountDatas) {


        perAccountDatas.stream().filter(data ->
                !data.getPerAccountPolicies().isEmpty())
                .forEach(data -> {

            AccountInfoForTransactionQueryResult account = response.
                    getAccount(data.getAccountId());

            data.getPerAccountPolicies().forEach(policy -> {
                if (policy.IS_INTERNAL())
                    account.expectedInternalAccount();
                if (policy.IS_BASE_CURRENCY())
                    account.expectedAccountCurrency(CurrencyCode.getBaseCurrency());
                if (policy.IS_FOREIGN_CURRENCY())
                    account.notExpectedAccountCurrency(CurrencyCode.getBaseCurrency());

            });
        });
    }

    public Mono<TransactionCreationPipelineResult> customLinearTransfer(
            final LinearInternalTransferPipelineData data) {
        return repository.getAccountsInfoForTransaction(data)
                .map(response ->
                        response.compact()
                                .expectedSize(data.getFixedSize())
                                .limitedQuoteCurrencySize(data.getMaximumQuoteCurrencySize()))
                .map(response -> {
                    applyTransferPolicyValidation(response, data.getTransferPolicies());

                    return response;
                })
                .flatMap(response ->
                        v1Module.defaultLinearCustomTransfer(response, data));
    }





}
