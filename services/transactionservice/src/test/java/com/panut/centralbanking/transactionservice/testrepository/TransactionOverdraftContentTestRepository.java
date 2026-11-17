package com.panut.centralbanking.transactionservice.testrepository;


import com.panut.centralbanking.transactionservice.infrastructure.model.datacontent.TransactionOverdraftContent;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.Collection;

public interface TransactionOverdraftContentTestRepository extends
        ReactiveCrudRepository<TransactionOverdraftContent, String> {



    Flux<TransactionOverdraftContent> findByTransactionId(String transactionId);

}
