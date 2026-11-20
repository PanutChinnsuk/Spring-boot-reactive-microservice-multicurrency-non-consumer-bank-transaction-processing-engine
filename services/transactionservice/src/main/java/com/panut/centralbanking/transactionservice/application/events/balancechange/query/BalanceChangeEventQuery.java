package com.panut.centralbanking.transactionservice.application.events.balancechange.query;

import com.panut.centralbanking.transactionservice.infrastructure.model.events.BalanceChangeEvent;
import com.panut.centralbanking.transactionservice.infrastructure.repository.BalanceChangeEventRepository;
import com.panut.centralbanking.transactionservice.infrastructure.repository.querydto.OptimizedBalanceChangeEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BalanceChangeEventQuery {
    private final BalanceChangeEventRepository repository;

    public BalanceChangeEventQuery(BalanceChangeEventRepository repository) {
        this.repository = repository;
    }


    public Mono<List<OptimizedBalanceChangeEvent>> getBalanceChangeEventsByTransactionId(
            final String transactionId
    ) {
        return repository.getByTransactionId(transactionId)
                .collectList();
    }
}
