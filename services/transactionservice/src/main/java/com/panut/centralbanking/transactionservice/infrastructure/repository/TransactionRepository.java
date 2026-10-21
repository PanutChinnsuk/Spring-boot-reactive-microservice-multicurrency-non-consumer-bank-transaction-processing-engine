package com.panut.centralbanking.transactionservice.infrastructure.repository;

import com.panut.centralbanking.transactionservice.infrastructure.model.Transaction;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface TransactionRepository extends ReactiveCrudRepository<Transaction, String> {
}
