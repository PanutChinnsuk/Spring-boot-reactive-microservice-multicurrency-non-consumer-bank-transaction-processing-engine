package com.panut.centralbanking.transactionservice.testrepository;


import com.panut.centralbanking.transactionservice.infrastructure.model.datacontent.TransactionMultiFxContent;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionMultiFxContentTestRepository extends
        ReactiveCrudRepository<TransactionMultiFxContent, String> {


    Flux<TransactionMultiFxContent> findByTransactionId(String transactionId);

    Mono<Void> deleteByTransactionId(String transactionId);
}
