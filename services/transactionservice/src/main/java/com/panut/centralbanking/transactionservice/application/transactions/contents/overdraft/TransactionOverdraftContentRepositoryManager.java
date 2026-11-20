package com.panut.centralbanking.transactionservice.application.transactions.contents.overdraft;

import com.panut.centralbanking.transactionservice.infrastructure.model.datacontent.TransactionOverdraftContent;
import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionOverdraftContentRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class TransactionOverdraftContentRepositoryManager {
    private final TransactionOverdraftContentRepository repository;

    public TransactionOverdraftContentRepositoryManager(TransactionOverdraftContentRepository repository) {
        this.repository = repository;
    }


    public Mono<TransactionOverdraftContent> save(final TransactionOverdraftContent content) {
        return repository.save(content);
    }
}
