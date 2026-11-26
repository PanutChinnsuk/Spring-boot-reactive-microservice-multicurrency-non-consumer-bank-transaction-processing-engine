package com.panut.centralbanking.transactionservice.testrepository;

import com.panut.centralbanking.transactionservice.infrastructure.model.datacontent.TransactionMultiFxDirectionContent;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionMultiFxDirectionContentTestRepository
extends ReactiveCrudRepository<TransactionMultiFxDirectionContent, String> {


    Flux<TransactionMultiFxDirectionContent> findByTransactionId(String transactionId);

    Mono<Void> deleteByTransactionId(String transactionId);
}
