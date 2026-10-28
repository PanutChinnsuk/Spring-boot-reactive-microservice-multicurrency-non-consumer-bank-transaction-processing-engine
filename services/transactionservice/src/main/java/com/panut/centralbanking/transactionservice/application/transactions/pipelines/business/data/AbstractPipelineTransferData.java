package com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.data;

import reactor.core.publisher.Mono;

import java.util.Set;

public abstract interface AbstractPipelineTransferData {
    public Mono<Void> validate();
    public int getFixedSize();
    Set<String> asAccountIds();
    Set<String> getLedgerCodeSet();
}
