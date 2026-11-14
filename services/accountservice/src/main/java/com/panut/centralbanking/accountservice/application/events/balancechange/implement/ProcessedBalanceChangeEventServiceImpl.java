package com.panut.centralbanking.accountservice.application.events.balancechange.implement;

import com.panut.centralbanking.accountservice.application.events.balancechange.service.ProcessedBalanceChangeEventService;
import com.panut.centralbanking.accountservice.infrastructure.repository.ProcessedBalanceChangeEventRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ProcessedBalanceChangeEventServiceImpl implements ProcessedBalanceChangeEventService {
    private final ProcessedBalanceChangeEventRepository repository;

    public ProcessedBalanceChangeEventServiceImpl(ProcessedBalanceChangeEventRepository repository) {
        this.repository = repository;
    }


    @Override
    public Mono<Set<String>> getAppliable(final List<String> eventIds) {
        return repository.getAppliable(eventIds)
                .collect(Collectors.toSet());
    }


}
