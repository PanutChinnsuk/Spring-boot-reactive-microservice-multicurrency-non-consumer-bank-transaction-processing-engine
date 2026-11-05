package com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.repository;

import com.panut.centralbanking.transactionservice.infrastructure.model.Transaction;
import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionLine;
import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Getter
public class TransactionManageChain {
    private Transaction transaction;
    private TransactionProfile transactionProfile;
    private Flux<TransactionLine> transactionLines;


    public Mono<TransactionManageChain> transaction(final Transaction transaction) {
        return Mono.defer(() -> {
            this.transaction = transaction;

            return Mono.just(this);
        });
    }

    public Mono<TransactionManageChain> transactionProfile(final TransactionProfile transactionProfile) {
        return Mono.defer(() -> {
            this.transactionProfile = transactionProfile;

            return Mono.just(this);
        });
    }

    public Mono<TransactionManageChain> transactionLines(final Flux<TransactionLine> transactionLines ) {
        return Mono.defer(() -> {
            this.transactionLines = transactionLines;

            return Mono.just(this);
        });
    }
}
