package com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.FxTransferPipeline.v1;

import com.panut.centralbanking.transactionservice.api.service.account.response.GetAccountsInfoForTransactionResponse;
import com.panut.centralbanking.transactionservice.api.service.fxRate.response.FxRateGetLatestResponse;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.FxRateData;
import lombok.Getter;
import reactor.core.publisher.Mono;

import java.util.Map;

@Getter
public class FxTransferPipelineChain {
    private FxRateGetLatestResponse fxRateGetLatestResponse;
    private Map<String, String> ledgerCodeMap;
    private FxRateData fxRateData;
    private GetAccountsInfoForTransactionResponse getAccountsInfoForTransactionResponse;


    public Mono<FxTransferPipelineChain> fxRateGetLatestResponse(final FxRateGetLatestResponse fxRateGetLatestResponse) {
        return Mono.defer(() -> {
            this.fxRateGetLatestResponse = fxRateGetLatestResponse;

            return Mono.just(this);
        });
    }

    public Mono<FxTransferPipelineChain> ledgerCodeMap(final Map<String, String> ledgerCodeMap) {
        return Mono.defer(() -> {
            this.ledgerCodeMap = ledgerCodeMap;

            return Mono.just(this);
        });
    }

    public Mono<FxTransferPipelineChain> fxRateData(final FxRateData fxRateData) {
        return Mono.defer(() -> {
            this.fxRateData = fxRateData;

            return Mono.just(this);
        });
    }

    public Mono<FxTransferPipelineChain> accountsInfoForTransactionResponse
            (final GetAccountsInfoForTransactionResponse getAccountsInfoForTransactionResponse) {
        return Mono.defer(() -> {
            this.getAccountsInfoForTransactionResponse = getAccountsInfoForTransactionResponse;

            return Mono.just(this);
        });
    }
}
