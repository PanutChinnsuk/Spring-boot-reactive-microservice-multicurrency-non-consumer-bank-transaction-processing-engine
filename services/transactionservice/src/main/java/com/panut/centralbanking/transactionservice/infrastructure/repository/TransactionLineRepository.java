package com.panut.centralbanking.transactionservice.infrastructure.repository;

import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionLine;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface TransactionLineRepository extends ReactiveCrudRepository<TransactionLine, String> {
}
