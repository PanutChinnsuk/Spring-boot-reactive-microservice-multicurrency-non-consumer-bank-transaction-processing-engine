package com.panut.centralbanking.transactionservice.application.transactions.transaction.service;

import com.panut.centralbanking.transactionservice.api.request.BaseBuyForeignTransferRequest;
import com.panut.centralbanking.transactionservice.api.request.ForeignBuyBaseTransferRequest;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.TransactionCreationPipelineResult;
import reactor.core.publisher.Mono;

public interface FxTransactionService {
    Mono<TransactionCreationPipelineResult> foreignBuyBase(ForeignBuyBaseTransferRequest r);
    Mono<TransactionCreationPipelineResult> baseBuyForeign(BaseBuyForeignTransferRequest r);
}
