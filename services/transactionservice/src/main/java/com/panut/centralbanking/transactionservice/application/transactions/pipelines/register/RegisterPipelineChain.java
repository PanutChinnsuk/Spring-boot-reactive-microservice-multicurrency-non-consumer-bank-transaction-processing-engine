package com.panut.centralbanking.transactionservice.application.transactions.pipelines.register;

import com.panut.centralbanking.transactionservice.api.service.response.FxRateGetLatestResponse;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.create.data.create.TransactionLineCreateData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Getter
class RegisterPipelineChain {
    private Map<String,String> ledgerAccounts;
    private FxRateGetLatestResponse fxRateGetLatestResponse;
    private List<TransactionLineCreateData> lineDatas;


    public boolean hasFxRate() {
        return fxRateGetLatestResponse != null;
    }


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

    public Mono<RegisterPipelineChain> lineDatas(List<TransactionLineCreateData> lineDatas) {
        return Mono.defer(() -> {
            this.lineDatas = lineDatas;
            return Mono.just(this);
        });
    }

}
