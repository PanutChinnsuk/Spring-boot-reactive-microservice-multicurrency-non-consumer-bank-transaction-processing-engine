package com.panut.centralbanking.transactionservice.application.transactions.line.query;

import com.panut.centralbanking.appcore.exceptions.NotFound;
import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionLine;
import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionLineRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class TransactionLineQuery {
    private final TransactionLineRepository transactionLineRepository;

    public TransactionLineQuery(TransactionLineRepository transactionLineRepository) {
        this.transactionLineRepository = transactionLineRepository;
    }


    public Flux<TransactionLine> findTransactionLines(final String transactionId) {
        return transactionLineRepository
                .findTransactionLines(transactionId)
                .switchIfEmpty(Mono.error(
                        () -> new NotFound("Transaction lines of transaction id " + transactionId
                        + " does not exist.")
                ));
    }
}
