package com.panut.centralbanking.transactionservice.application.transactions.pipelines.create;

import com.panut.centralbanking.transactionservice.infrastructure.model.Transaction;
import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionProfile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

@Getter
@AllArgsConstructor
@NoArgsConstructor
class CreationChain {
    private Transaction transaction;
    private TransactionProfile transactionProfile;



    public Mono<CreationChain> transaction(final Transaction transaction) {
        return Mono.defer(() -> {
            this.transaction = transaction;
            return Mono.just(this);
        });
    }

    public Mono<CreationChain> transactionProfile(final TransactionProfile transactionProfile) {
        return Mono.defer(() -> {
            this.transactionProfile = transactionProfile;
            return Mono.just(this);
        });
    }

}
