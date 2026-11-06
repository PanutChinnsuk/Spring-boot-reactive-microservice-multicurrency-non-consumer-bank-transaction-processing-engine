package com.panut.centralbanking.transactionservice.application.transactions.clearing.query.data;

import com.panut.centralbanking.transactionservice.infrastructure.model.Transaction;
import com.panut.centralbanking.transactionservice.infrastructure.model.events.BalanceChangeEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;


public class TransactionBalanceClearingData {
    private String transactonId;
    private Flux<BalanceChangeEvent> balanceChangeEvents;

}
