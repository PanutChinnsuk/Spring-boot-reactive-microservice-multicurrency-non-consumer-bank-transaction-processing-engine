package com.panut.centralbanking.transactionservice.application.events.balancechange;

import com.panut.centralbanking.transactionservice.infrastructure.model.events.BalanceChangeEvent;
import com.panut.centralbanking.transactionservice.infrastructure.repository.BalanceChangeEventRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

@Component
public class BalanceChangeEventRepositoryManager {
    private final BalanceChangeEventRepository balanceChangeEventRepository;

    public BalanceChangeEventRepositoryManager(BalanceChangeEventRepository balanceChangeEventRepository) {
        this.balanceChangeEventRepository = balanceChangeEventRepository;
    }


    public Flux<BalanceChangeEvent> saveAll(final Collection<BalanceChangeEvent> events) {
        return balanceChangeEventRepository.saveAll(events);
    }
}
