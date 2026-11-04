package com.panut.centralbanking.transactionservice.application.transactions.profile.query;

import com.panut.centralbanking.appcore.exceptions.NotFound;
import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionProfile;
import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionProfileRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class TransactionProfileQuery {
    private final TransactionProfileRepository transactionProfileRepository;


    public TransactionProfileQuery(TransactionProfileRepository transactionProfileRepository) {
        this.transactionProfileRepository = transactionProfileRepository;
    }


    public Mono<TransactionProfile> findTransactionProfileById(final String transactionId) {
        return transactionProfileRepository.findTransactionProfileById(transactionId)
                .switchIfEmpty(Mono.error(
                        () -> new NotFound("Transaction profile for transaction id of " + transactionId +
                                " does not exist.")
                ));
    }
}
