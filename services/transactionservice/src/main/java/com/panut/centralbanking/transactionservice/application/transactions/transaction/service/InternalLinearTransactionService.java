package com.panut.centralbanking.transactionservice.application.transactions.transaction.service;

import com.panut.centralbanking.transactionservice.api.request.InternalLinearTransferRequest;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.TransactionCreationPipelineResult;
import reactor.core.publisher.Mono;

public interface InternalLinearTransactionService {
    Mono<TransactionCreationPipelineResult> linear(InternalLinearTransferRequest r);
}
