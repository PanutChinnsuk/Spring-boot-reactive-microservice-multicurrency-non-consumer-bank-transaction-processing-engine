package com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.create;

import com.panut.centralbanking.transactionservice.infrastructure.model.Transaction;
import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionLine;
import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionProfile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
class CreationPipelineChain {
    private Transaction transaction;
    private TransactionProfile transactionProfile;
    private List<TransactionLine> transactionLines;



    public Mono<CreationPipelineChain> transaction(final Transaction transaction) {
        return Mono.defer(() -> {
            this.transaction = transaction;
            return Mono.just(this);
        });
    }

    public Mono<CreationPipelineChain> transactionProfile(final TransactionProfile transactionProfile) {
        return Mono.defer(() -> {
            this.transactionProfile = transactionProfile;
            return Mono.just(this);
        });
    }

    public Mono<CreationPipelineChain> transactionLines(final List<TransactionLine> transactionLines) {
        return Mono.defer(() -> {
            this.transactionLines = transactionLines;
            return Mono.just(this);
        });
    }

}
