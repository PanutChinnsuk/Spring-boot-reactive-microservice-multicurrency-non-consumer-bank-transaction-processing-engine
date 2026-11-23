package com.panut.centralbanking.transactionservice.application.transactions.transaction.query;

import com.panut.centralbanking.appcore.exceptions.NotFound;
import com.panut.centralbanking.transactionservice.core.TransactionStatus;
import com.panut.centralbanking.transactionservice.infrastructure.model.Transaction;
import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
public class TransactionQuery {
    private final TransactionRepository transactionRepository;

    public TransactionQuery(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }


    public Mono<Transaction> findById(final String transactionId,
                                      final TransactionStatus... expectedStatuses) {
        return transactionRepository.findTransactionById(transactionId,
                        Arrays.asList(expectedStatuses))
                .switchIfEmpty(Mono.error(
                        () -> new NotFound("Transaction id of " + transactionId + " not found " +
                                "with expected status of " + expectedStatuses.toString())
                ));
    }

    public Mono<Transaction> getReversed(final String transactionId) {
        return transactionRepository.getReversedTransactionId(transactionId);
    }
}
