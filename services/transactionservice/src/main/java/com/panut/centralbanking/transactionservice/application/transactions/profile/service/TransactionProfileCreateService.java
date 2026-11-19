package com.panut.centralbanking.transactionservice.application.transactions.profile.service;

import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.TransactionReversalContext;
import com.panut.centralbanking.transactionservice.application.transactions.profile.TransactionProfileRepositoryManager;
import com.panut.centralbanking.transactionservice.core.FxDirection;
import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionProfile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class TransactionProfileCreateService {
    private final TransactionProfileRepositoryManager manager;

    public TransactionProfileCreateService(TransactionProfileRepositoryManager manager) {
        this.manager = manager;
    }


    private Mono<TransactionProfile> create(final String transactionId,
                                            final FxDirection fxDirection,
                                            final String fxRateId,
                                            final TransactionReversalContext transactionReversalContext) {
        return manager.save(TransactionProfile
                .builder()
                .transactionId(transactionId)
                .fxDirection(fxDirection)
                .fxRateId(fxRateId)
                .build()
                .applyWithTransactionReversalContext(transactionReversalContext));
    }


    public Mono<TransactionProfile> createProfile(final String transactionId,
                                                  final FxDirection fxDirection,
                                                  final String fxRateId,
                                                  final TransactionReversalContext transactionReversalContext) {
        return create(transactionId, fxDirection, fxRateId,
                transactionReversalContext);
    }


}
