package com.panut.centralbanking.transactionservice.application.transactions.contents.multifxdirection;

import com.panut.centralbanking.transactionservice.infrastructure.model.datacontent.TransactionMultiFxDirectionContent;
import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionMultiFxDirectionContentRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Collection;

@Component
public class TransactionMultiFxDirectionContentRepositoryManager {
    private final TransactionMultiFxDirectionContentRepository repository;

    public TransactionMultiFxDirectionContentRepositoryManager(TransactionMultiFxDirectionContentRepository repository) {
        this.repository = repository;
    }



    public Flux<TransactionMultiFxDirectionContent> saveAll(final Collection<TransactionMultiFxDirectionContent> contents) {
        return repository.saveAll(contents);
    }
}
