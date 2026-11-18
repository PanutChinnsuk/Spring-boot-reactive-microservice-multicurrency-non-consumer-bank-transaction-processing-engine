package com.panut.centralbanking.transactionservice.application.events.balancechange.service;

import com.panut.centralbanking.transactionservice.api.service.account.data.related.BalanceChangeType;
import com.panut.centralbanking.transactionservice.api.service.account.data.related.BalanceImpact;
import com.panut.centralbanking.transactionservice.application.events.balancechange.BalanceChangeEventRepositoryManager;
import com.panut.centralbanking.transactionservice.application.events.balancechange.ChangingBalance;
import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionLine;
import com.panut.centralbanking.transactionservice.infrastructure.model.events.BalanceChangeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Collection;

@Service
@Slf4j
public class BalanceChangeEventService {
    private final BalanceChangeEventRepositoryManager manager;

    public BalanceChangeEventService(BalanceChangeEventRepositoryManager manager) {
        this.manager = manager;
    }


    private BalanceChangeEvent put(final String accountId,
                                         final BigDecimal amount,
                                         final BalanceImpact balanceImpact,
                                         final BalanceChangeType balanceChangeType,
                                        final String transactionId) {
        return BalanceChangeEvent
                .builder()
                .accountId(accountId)
                .amount(amount)
                .balanceImpact(balanceImpact)
                .balanceChangeType(balanceChangeType)
                .transactionId(transactionId)
                .build();
    }


    public BalanceChangeEvent create(final String accountId,
                                           final BigDecimal amount,
                                           final String transactionId,
                                           final ChangingBalance changingBalance) {
        return put(accountId, amount, changingBalance.getBalanceImpact(),
                changingBalance.getBalanceChangeType(), transactionId);
    }

    public BalanceChangeEvent create(final TransactionLine line,
                                           final ChangingBalance changingBalance) {
        BigDecimal changingAmount = line.getConvertedAmount();

        var impact = changingBalance.getBalanceImpact();
        var type = changingBalance.getBalanceChangeType();

        return put(line.getAccountId(),
                changingAmount,
                impact,
                type,
                line.getTransactionId());
    }


    public Flux<BalanceChangeEvent> flush(final Collection<BalanceChangeEvent> events) {
        return manager.saveAll(events);
    }


}
