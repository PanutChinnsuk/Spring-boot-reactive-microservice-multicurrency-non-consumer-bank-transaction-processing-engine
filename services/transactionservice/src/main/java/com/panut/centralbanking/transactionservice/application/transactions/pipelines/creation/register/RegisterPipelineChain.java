package com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register;

import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.create.data.TransactionLineCreateData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@NoArgsConstructor
@Getter
class RegisterPipelineChain {
    private List<TransactionLineCreateData> lineDatas;






    public Mono<RegisterPipelineChain> lineDatas(List<TransactionLineCreateData> lineDatas) {
        return Mono.defer(() -> {
            this.lineDatas = lineDatas;
            return Mono.just(this);
        });
    }

}
