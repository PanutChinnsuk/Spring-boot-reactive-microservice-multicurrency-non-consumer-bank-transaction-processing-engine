package com.panut.centralbanking.transactionservice.infrastructure.repository;


import com.panut.centralbanking.transactionservice.infrastructure.model.datacontent.TransactionMultiFxContent;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface TransactionMultiFxContentRepository extends
        ReactiveCrudRepository<TransactionMultiFxContent, String> {


    Flux<TransactionMultiFxContent> findByTransactionId(String transactionId);
}
