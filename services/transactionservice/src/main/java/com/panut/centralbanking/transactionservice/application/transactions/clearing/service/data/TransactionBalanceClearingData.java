package com.panut.centralbanking.transactionservice.application.transactions.clearing.service.data;

import com.panut.centralbanking.transactionservice.infrastructure.model.events.BalanceChangeEvent;
import reactor.core.publisher.Flux;


public class TransactionBalanceClearingData {
    private String transactonId;
    private Flux<BalanceChangeEvent> balanceChangeEvents;

}
