package com.panut.centralbanking.transactionservice.application.transactions.transaction;

import com.panut.centralbanking.transactionservice.infrastructure.model.Transaction;
import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class TransactionRepositoryManager {
    private final TransactionRepository transactionRepository;

    public TransactionRepositoryManager(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }


    public Mono<Transaction> save(final Transaction transaction) {
        return this.transactionRepository.save(transaction);
    }
}
