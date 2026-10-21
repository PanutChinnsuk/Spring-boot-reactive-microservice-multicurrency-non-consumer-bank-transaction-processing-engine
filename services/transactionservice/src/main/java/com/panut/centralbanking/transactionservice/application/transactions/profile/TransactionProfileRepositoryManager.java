package com.panut.centralbanking.transactionservice.application.transactions.profile;

import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionProfile;
import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionProfileRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class TransactionProfileRepositoryManager {
    private final TransactionProfileRepository transactionProfileRepository;

    public TransactionProfileRepositoryManager(TransactionProfileRepository transactionProfileRepository) {
        this.transactionProfileRepository = transactionProfileRepository;
    }


    public Mono<TransactionProfile> save(final TransactionProfile transactionProfile) {
        return this.transactionProfileRepository.save(transactionProfile);
    }
}
