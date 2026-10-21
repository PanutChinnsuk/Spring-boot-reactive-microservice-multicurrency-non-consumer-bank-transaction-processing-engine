package com.panut.centralbanking.transactionservice.application.transactions.pipelines.register;

import com.panut.centralbanking.transactionservice.api.service.response.FxRateGetLatestResponse;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Map;

@NoArgsConstructor
class RegisterPipelineChain {
    private Map<String,String> ledgerAccounts;
    private FxRateGetLatestResponse fxRateGetLatestResponse;



    public Mono<RegisterPipelineChain> ledgerAccounts(Map<String,String> ledgerAccounts) {
        return Mono.defer(() -> {
            this.ledgerAccounts = ledgerAccounts;
            return Mono.just(this);
        });
    }

    public Mono<RegisterPipelineChain> fxRateGetLatestResponse(FxRateGetLatestResponse fxRateGetLatestResponse) {
        return Mono.defer(() -> {
            this.fxRateGetLatestResponse = fxRateGetLatestResponse;
            return Mono.just(this);
        });
    }

}
