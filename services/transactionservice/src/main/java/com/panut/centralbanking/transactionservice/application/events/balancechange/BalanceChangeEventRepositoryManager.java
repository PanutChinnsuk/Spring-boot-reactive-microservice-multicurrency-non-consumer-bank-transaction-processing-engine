package com.panut.centralbanking.transactionservice.application.events.balancechange;

import com.panut.centralbanking.transactionservice.infrastructure.model.events.BalanceChangeEvent;
import com.panut.centralbanking.transactionservice.infrastructure.repository.BalanceChangeEventRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class BalanceChangeEventRepositoryManager {
    private final BalanceChangeEventRepository balanceChangeEventRepository;

    public BalanceChangeEventRepositoryManager(BalanceChangeEventRepository balanceChangeEventRepository) {
        this.balanceChangeEventRepository = balanceChangeEventRepository;
    }


    public Mono<BalanceChangeEvent> save(final BalanceChangeEvent event) {
        return balanceChangeEventRepository.save(event);
    }
}
