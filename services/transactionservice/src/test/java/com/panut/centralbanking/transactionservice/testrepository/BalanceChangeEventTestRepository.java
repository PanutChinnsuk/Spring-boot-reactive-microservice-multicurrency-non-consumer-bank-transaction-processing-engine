package com.panut.centralbanking.transactionservice.testrepository;

import com.panut.centralbanking.transactionservice.infrastructure.model.events.BalanceChangeEvent;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BalanceChangeEventTestRepository
        extends ReactiveCrudRepository<BalanceChangeEvent, String> {


    Mono<Void> deleteByTransactionId(String transactionId);

    Flux<BalanceChangeEvent> findByTransactionId(String transactionId);
}
