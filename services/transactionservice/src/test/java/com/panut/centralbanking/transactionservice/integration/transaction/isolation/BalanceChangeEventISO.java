package com.panut.centralbanking.transactionservice.integration.transaction.isolation;

import com.panut.centralbanking.transactionservice.api.service.account.data.related.BalanceChangeType;
import com.panut.centralbanking.transactionservice.api.service.account.data.related.BalanceImpact;
import com.panut.centralbanking.transactionservice.core.BalanceChangeEventStatus;
import com.panut.centralbanking.transactionservice.infrastructure.model.Transaction;
import com.panut.centralbanking.transactionservice.infrastructure.model.events.BalanceChangeEvent;
import com.panut.centralbanking.transactionservice.infrastructure.repository.BalanceChangeEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BalanceChangeEventISO {
    @Autowired
    private BalanceChangeEventRepository eventRepository;


    public BalanceChangeEvent balanceChangeEvent(Transaction transaction,
                                                 String accountId,
                                                 BigDecimal amount,
                                                 BalanceChangeEventStatus status,
                                                 BalanceImpact balanceImpact,
                                                 BalanceChangeType balanceChangeType,
                                                 String customEventId) {
        var event = BalanceChangeEvent.builder()
                .transactionId(transaction.getId())
                .id(customEventId)
                .balanceImpact(balanceImpact)
                .accountId(accountId)
                .balanceChangeType(balanceChangeType)
                .amount(amount)
                .status(status)
                .build();

        return eventRepository.save(event).block();
    }

    public BalanceChangeEvent balanceChangeEvent(Transaction transaction,
                                                 String accountId,
                                                 BigDecimal amount,
                                                 BalanceImpact balanceImpact,
                                                 BalanceChangeType balanceChangeType) {
       return balanceChangeEvent(transaction, accountId, amount, null,
                balanceImpact, balanceChangeType, null);
    }
}
