package com.panut.centralbanking.accountservice.application.events.processedbalancechange.implement;

import com.panut.centralbanking.accountservice.application.events.processedbalancechange.service.ProcessedBalanceChangeEventService;
import com.panut.centralbanking.accountservice.application.worker.WorkerService;
import com.panut.centralbanking.accountservice.core.WorkerName;
import com.panut.centralbanking.accountservice.infrastructure.repository.ProcessedBalanceChangeEventRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ProcessedBalanceChangeEventServiceImpl implements ProcessedBalanceChangeEventService {
    private final ProcessedBalanceChangeEventRepository repository;
    private final WorkerService workerService;

    public ProcessedBalanceChangeEventServiceImpl(ProcessedBalanceChangeEventRepository repository,
                                                  WorkerService workerService) {
        this.repository = repository;
        this.workerService = workerService;
    }


    @Override
    public Mono<Set<String>> getAppliable(final List<String> eventIds) {
        return workerService.initWorker(WorkerName.TRANSACTION)
                .then(repository.getAppliable(eventIds)
                        .collect(Collectors.toSet()));
    }



}
