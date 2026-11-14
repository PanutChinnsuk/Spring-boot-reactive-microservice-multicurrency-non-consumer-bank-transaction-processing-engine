package com.panut.centralbanking.accountservice.application.events.balancechange.query;

import com.panut.centralbanking.accountservice.infrastructure.repository.ProcessedBalanceChangeEventRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ProcessedBalanceChangeEventQuery {
    private final ProcessedBalanceChangeEventRepository repository;

    public ProcessedBalanceChangeEventQuery(ProcessedBalanceChangeEventRepository repository) {
        this.repository = repository;
    }


    public Mono<Set<String>> getAlreadyProcessedEventIds(final List<String> eventIds) {
        return repository.getProcessed(eventIds)
                .collect(Collectors.toSet());
    }


}
