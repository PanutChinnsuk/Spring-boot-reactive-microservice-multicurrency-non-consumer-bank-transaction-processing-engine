package com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline;

import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.transactionservice.api.service.account.response.GetAccountsInfoForTransactionResponse;
import com.panut.centralbanking.transactionservice.api.service.account.response.queryresult.AccountInfoForTransactionQueryResult;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.policies.CustomTransferDataPolicy;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.v1.data.CustomAccountTransferData;

import java.util.List;
import java.util.Set;

public class InternalTransferPipelinePolicies {

    public static void applyTransferPolicyValidation(final GetAccountsInfoForTransactionResponse response,
                                               final Set<CustomTransferDataPolicy> policies) {
        if (policies == null) return;
        if (policies.isEmpty()) return;

        policies.forEach(policy -> {
            if (policy.ACTIVE_ACCOUNTS_ONLY())
                response.expectedAllAccountStatusActive();
        });

    }

    public static void applyPerAccountPolicyValidation(final GetAccountsInfoForTransactionResponse response,
                                                 final List<CustomAccountTransferData> perAccountDatas) {
        perAccountDatas.stream().filter(data ->
                        data.getPerAccountPolicies() != null &&
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
}
