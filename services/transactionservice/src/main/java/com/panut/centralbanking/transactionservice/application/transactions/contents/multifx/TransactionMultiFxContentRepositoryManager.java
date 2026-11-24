package com.panut.centralbanking.transactionservice.application.transactions.contents.multifx;

import com.panut.centralbanking.transactionservice.infrastructure.model.datacontent.TransactionMultiFxContent;
import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionMultiFxContentRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Collection;

@Component
public class TransactionMultiFxContentRepositoryManager {
    private final TransactionMultiFxContentRepository repository;

    public TransactionMultiFxContentRepositoryManager(TransactionMultiFxContentRepository repository) {
        this.repository = repository;
    }


    public Flux<TransactionMultiFxContent> saveAll(final Collection<TransactionMultiFxContent> contents) {
        return repository.saveAll(contents);
    }
}
