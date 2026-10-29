package com.panut.centralbanking.transactionservice.integration.testrepository;

import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionLine;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface TransactionLineTestRepository extends ReactiveCrudRepository<TransactionLine, String> {

    Flux<TransactionLine> findByTransactionId(String transactionId);

}
