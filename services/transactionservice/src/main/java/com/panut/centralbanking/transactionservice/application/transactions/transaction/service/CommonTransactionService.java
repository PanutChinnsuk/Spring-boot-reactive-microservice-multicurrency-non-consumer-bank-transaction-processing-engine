package com.panut.centralbanking.transactionservice.application.transactions.transaction.service;

import com.panut.centralbanking.transactionservice.api.request.SameCurrencyTransferRequest;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.TransactionCreationPipelineResult;
import reactor.core.publisher.Mono;

public interface CommonTransactionService {
    Mono<TransactionCreationPipelineResult> sameCurrencyTransfer(SameCurrencyTransferRequest r);
    Mono<TransactionCreationPipelineResult> sameCurrencyExpense(SameCurrencyTransferRequest r);
    Mono<TransactionCreationPipelineResult> sameCurrencyAsset(SameCurrencyTransferRequest r);
}
