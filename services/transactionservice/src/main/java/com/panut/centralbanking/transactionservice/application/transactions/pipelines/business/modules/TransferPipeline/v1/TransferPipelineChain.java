package com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.TransferPipeline.v1;

import com.panut.centralbanking.transactionservice.api.service.account.response.GetAccountsInfoForTransactionResponse;
import com.panut.centralbanking.transactionservice.api.service.fxRate.response.FxRateGetResponse;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.FxRateData;
import lombok.Getter;
import reactor.core.publisher.Mono;

import java.util.Map;

@Getter
public class TransferPipelineChain {
    private FxRateGetResponse fxRateGetResponse;
    private Map<String, String> ledgerCodeMap;
    private FxRateData fxRateData;
    private GetAccountsInfoForTransactionResponse getAccountsInfoForTransactionResponse;


    public Mono<TransferPipelineChain> fxRateGetLatestResponse(final FxRateGetResponse fxRateGetResponse) {
        return Mono.defer(() -> {
            this.fxRateGetResponse = fxRateGetResponse;

            return Mono.just(this);
        });
    }

    public Mono<TransferPipelineChain> ledgerCodeMap(final Map<String, String> ledgerCodeMap) {
        return Mono.defer(() -> {
            this.ledgerCodeMap = ledgerCodeMap;

            return Mono.just(this);
        });
    }

    public Mono<TransferPipelineChain> fxRateData(final FxRateData fxRateData) {
        return Mono.defer(() -> {
            this.fxRateData = fxRateData;

            return Mono.just(this);
        });
    }

    public Mono<TransferPipelineChain> accountsInfoForTransactionResponse
            (final GetAccountsInfoForTransactionResponse getAccountsInfoForTransactionResponse) {
        return Mono.defer(() -> {
            this.getAccountsInfoForTransactionResponse = getAccountsInfoForTransactionResponse;

            return Mono.just(this);
        });
    }
}
