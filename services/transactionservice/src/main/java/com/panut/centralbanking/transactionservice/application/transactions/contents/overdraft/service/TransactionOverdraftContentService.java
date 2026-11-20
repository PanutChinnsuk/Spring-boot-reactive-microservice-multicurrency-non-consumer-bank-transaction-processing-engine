package com.panut.centralbanking.transactionservice.application.transactions.contents.overdraft.service;

import com.panut.centralbanking.transactionservice.application.transactions.contents.overdraft.TransactionOverdraftContentRepositoryManager;
import com.panut.centralbanking.transactionservice.infrastructure.model.datacontent.TransactionOverdraftContent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class TransactionOverdraftContentService {
    private final TransactionOverdraftContentRepositoryManager manager;

    public TransactionOverdraftContentService(TransactionOverdraftContentRepositoryManager manager) {
        this.manager = manager;
    }



    public Mono<TransactionOverdraftContent> include(final String accountOverdraftId,
                                                     final String transactionId) {
        return manager.save(TransactionOverdraftContent.builder()
                .transactionId(transactionId)
                .overdraftId(accountOverdraftId)
                .build());
    }
}
