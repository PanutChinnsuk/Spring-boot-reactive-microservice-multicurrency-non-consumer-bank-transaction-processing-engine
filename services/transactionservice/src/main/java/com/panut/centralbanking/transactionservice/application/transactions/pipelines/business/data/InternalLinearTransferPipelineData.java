package com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.data;

import com.panut.centralbanking.appcore.exceptions.BusinessException;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.policies.CustomTransferDataPolicy;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.policies.ledger.CustomTransferLedgerPolicy;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.v1.data.CustomAccountTransferData;
import lombok.Getter;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class InternalLinearTransferPipelineData implements AbstractPipelineTransferData {
    private final List<CustomAccountTransferData> customDatas;
    private final Set<CustomTransferDataPolicy> transferPolicies;
    private final List<CustomTransferLedgerPolicy> ledgerPolicies;


    private final int maximumQuoteCurrencySize = 1;
    private final int fixedMinimumSize = 2;
    private final int fixedSize;
    private final Set<String> accountIds;
    private final Set<String> ledgerCodes;


    public InternalLinearTransferPipelineData(List<CustomAccountTransferData> customDatas,
                                              Set<CustomTransferDataPolicy> transferPolicies,
                                              List<CustomTransferLedgerPolicy> ledgerPolicies) {
        this.customDatas = customDatas;
        this.transferPolicies = transferPolicies;
        this.ledgerPolicies = ledgerPolicies;

        accountIds = customDatas
                .stream().map(CustomAccountTransferData::getAccountId)
                .collect(Collectors.toSet());

        ledgerCodes = customDatas
                .stream().map(data -> data.getLedgerCode().getValue())
                .collect(Collectors.toSet());


        this.fixedSize = accountIds.size();
    }


    @Override
    public Mono<Void> validate() {
        return Mono.defer(() -> {
            if (customDatas.size() < fixedMinimumSize)
                return Mono.error(
                        () -> new BusinessException("CustomAccountTransferData list size must have " +
                                "atleast size of " + fixedMinimumSize));

            return Mono.empty();
        });
    }



    @Override
    public Set<String> asAccountIds() {
        return accountIds;
    }

    @Override
    public Set<String> getLedgerCodeSet() {
        return ledgerCodes;
    }

    public boolean requiredFxRate() {
        return customDatas.stream()
                .anyMatch(data -> data.hasFxContext());
    }


}
