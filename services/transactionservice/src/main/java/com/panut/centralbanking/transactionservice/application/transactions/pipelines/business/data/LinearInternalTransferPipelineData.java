package com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.data;

import com.panut.centralbanking.appcore.exceptions.BusinessException;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.CustomAccountTransferDataPolicy;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.v1.data.CustomAccountTransferData;
import lombok.Getter;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class LinearInternalTransferPipelineData implements AbstractPipelineTransferData {
    private final List<CustomAccountTransferData> customDatas;
    private final List<CustomAccountTransferDataPolicy> transferPolicies;

    private final int maximumQuoteCurrencySize= 1;
    private final int fixedMinimumSize = 2;
    private final int fixedSize;
    private final Set<String> accountIds;
    private final Set<String> ledgerCodes;


    public LinearInternalTransferPipelineData(List<CustomAccountTransferData> customDatas,
                                              List<CustomAccountTransferDataPolicy> transferPolicies) {
        this.customDatas = customDatas;
        this.transferPolicies = transferPolicies;

        this.fixedSize = customDatas.size();

        accountIds = customDatas
                .stream().map(CustomAccountTransferData::getAccountId)
                .collect(Collectors.toSet());

        ledgerCodes = customDatas
                .stream().map(data -> data.getLedgerCode().getValue())
                .collect(Collectors.toSet());
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


}
