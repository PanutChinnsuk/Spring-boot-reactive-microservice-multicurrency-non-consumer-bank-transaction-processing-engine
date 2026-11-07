package com.panut.centralbanking.transactionservice.application.transactions.clearing.service;

import com.panut.centralbanking.transactionservice.api.service.account.data.BalanceChangeData;
import reactor.core.publisher.Flux;

public interface TransactionClearingService {
    Flux<BalanceChangeData> getBulkPending();
}
