package com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre;

import com.panut.centralbanking.transactionservice.api.service.fxRate.response.FxRateMultiGetResponse;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.TransferPipeline.TransferMultiGetPipelineChain;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.TransactionMultiFxContext;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.TransactionReversalContext;
import com.panut.centralbanking.transactionservice.core.FxDirection;
import com.panut.centralbanking.transactionservice.core.TransactionType;
import com.panut.centralbanking.transactionservice.infrastructure.model.datacontent.TransactionMultiFxContent;
import com.panut.centralbanking.transactionservice.infrastructure.model.datacontent.TransactionMultiFxDirectionContent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Getter
public class PreTransactionRegisterData {
    private final TransactionType transactionType;
    private final List<AccountData> accountDatas;
    private final Set<String> usingAccountIds;
    private final FxRateData fxRateData;
    private TransactionReversalContext transactionReversalContext;
    private TransactionMultiFxContext transactionMultiFxContext;




    public PreTransactionRegisterData(TransactionType transactionType,
                                      List<AccountData> accountDatas,
                                      FxRateData fxRateData) {
        this.transactionType = transactionType;
        this.accountDatas = accountDatas;
        this.usingAccountIds = accountDatas.stream()
                .map(AccountData::getAccountId).collect(Collectors.toSet());

        this.fxRateData = fxRateData;
    }


    public FxRateData getFxRateData() {
        return fxRateData;
    }

    public PreTransactionRegisterData withTransactionReversalContext(
            final TransactionReversalContext transactionReversalContext
    ) {
        this.transactionReversalContext = transactionReversalContext;

        return this;
    }

    public PreTransactionRegisterData withTransactionMultiFxContext(
            final TransactionMultiFxContext transactionMultiFxContext
    ) {
        this.transactionMultiFxContext = transactionMultiFxContext;

        return this;
    }

    public PreTransactionRegisterData withTransactionMultiFxContext(
            final TransferMultiGetPipelineChain chain,
            final Map<String, AccountData> finalizedAccountDatas
            ) {
        var fxRateMultiGetResponse = chain.getFxRateMultiGetResponse();

        if (!chain.hasFxDirection()) {
            log.info("PreTransactionRegisterData does not required auto withTransactionMultiFxContext");
            return this;
        }


        List<TransactionMultiFxContent> contents =
                fxRateMultiGetResponse.getFxRateMap()
                        .values()
                        .stream()
                        .map(fxRate -> TransactionMultiFxContent.builder()
                                .fxRateId(fxRate.getFxRateId())
                                .build()

                        ).toList();

        List<TransactionMultiFxDirectionContent> directionContents =
                finalizedAccountDatas
                        .values()
                        .stream()
                        .filter(data -> data.hasPassedTransferDataFxContext())
                                .map(data -> {
                                    var passedFxContext = data
                                            .getPassedTransferDataFxContext();

                                    return TransactionMultiFxDirectionContent
                                            .builder()
                                            .fxDirection(passedFxContext.getDirectionForFx())
                                            .transactionLineId(data.getObjectIdentity())
                                            .build();
                                }).toList();

        log.info("fxRateMultiGetResponse required auto withTransactionMultiFxContext, content size: {}, content direction size: {}",
                directionContents.size(),
                contents.size());

        var context = new TransactionMultiFxContext(contents, directionContents);
        this.transactionMultiFxContext = context;


        return this;
    }

    public PreTransactionRegisterData withTransactionMultiFxContext(
            final TransferMultiGetPipelineChain chain
    ) {
        return withTransactionMultiFxContext(chain, null);
    }
}
