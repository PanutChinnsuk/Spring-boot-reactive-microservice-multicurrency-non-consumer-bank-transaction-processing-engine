package com.panut.centralbanking.transactionservice.application.transactions.transaction.service;

import com.panut.centralbanking.transactionservice.application.transactions.profile.service.TransactionProfileCreateService;
import com.panut.centralbanking.transactionservice.application.transactions.transaction.TransactionRepositoryManager;
import com.panut.centralbanking.transactionservice.core.TransactionType;
import com.panut.centralbanking.transactionservice.infrastructure.model.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
public class TransactionCreateService {
    private final TransactionRepositoryManager manager;


    public TransactionCreateService(TransactionRepositoryManager manager) {
        this.manager = manager;
    }


    public Mono<Transaction> createTransaction(final TransactionType transactionType) {
        return manager.save(Transaction.builder()
                .transactionType(transactionType)
                .build());
    }


}
