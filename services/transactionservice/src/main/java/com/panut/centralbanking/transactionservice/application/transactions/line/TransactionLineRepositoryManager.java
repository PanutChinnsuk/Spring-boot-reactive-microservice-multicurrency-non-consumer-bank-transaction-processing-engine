package com.panut.centralbanking.transactionservice.application.transactions.line;

import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionLine;
import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionLineRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
public class TransactionLineRepositoryManager {
    private final TransactionLineRepository transactionLineRepository;

    public TransactionLineRepositoryManager(TransactionLineRepository transactionLineRepository) {
        this.transactionLineRepository = transactionLineRepository;
    }


    public Mono<TransactionLine> save(final TransactionLine transactionLine) {
        return this.transactionLineRepository.save(transactionLine);
    }

    public Mono<List<TransactionLine>> saveAll(final Collection<TransactionLine> transactionLine) {
        return this.transactionLineRepository.saveAll(transactionLine)
                .collectList();
    }
}
