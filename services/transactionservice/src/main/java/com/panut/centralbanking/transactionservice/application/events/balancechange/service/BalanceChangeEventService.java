package com.panut.centralbanking.transactionservice.application.events.balancechange.service;

import com.panut.centralbanking.transactionservice.api.service.account.data.related.BalanceChangeType;
import com.panut.centralbanking.transactionservice.api.service.account.data.related.BalanceImpact;
import com.panut.centralbanking.transactionservice.application.events.balancechange.BalanceChangeEventRepositoryManager;
import com.panut.centralbanking.transactionservice.application.events.balancechange.ChangingBalance;
import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionLine;
import com.panut.centralbanking.transactionservice.infrastructure.model.events.BalanceChangeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
@Slf4j
public class BalanceChangeEventService {
    private final BalanceChangeEventRepositoryManager manager;

    public BalanceChangeEventService(BalanceChangeEventRepositoryManager manager) {
        this.manager = manager;
    }


    private Mono<BalanceChangeEvent> put(final String accountId,
                                         final BigDecimal amount,
                                         final BalanceImpact balanceImpact,
                                         final BalanceChangeType balanceChangeType,
                                        final String transactionId) {
        return manager.save(BalanceChangeEvent
                .builder()
                .accountId(accountId)
                .amount(amount)
                .balanceImpact(balanceImpact)
                .balanceChangeType(balanceChangeType)
                .transactionId(transactionId)
                .build())
                .doOnNext(a -> log
                        .info("Created balance change event."));
    }


    public Mono<BalanceChangeEvent> create(final String accountId,
                                           final BigDecimal amount,
                                           final String transactionId,
                                           final ChangingBalance changingBalance) {
        return put(accountId, amount, changingBalance.getBalanceImpact(),
                changingBalance.getBalanceChangeType(), transactionId);
    }

    public Mono<BalanceChangeEvent> create(final TransactionLine line,
                                           final ChangingBalance changingBalance) {
        return put(line.getAccountId(),
                line.getConvertedAmount(),
                changingBalance.getBalanceImpact(),
                changingBalance.getBalanceChangeType(),
                line.getTransactionId());
    }


}
