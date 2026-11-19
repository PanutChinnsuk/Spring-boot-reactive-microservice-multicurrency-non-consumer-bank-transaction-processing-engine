package com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.authorization.validator;

import com.panut.centralbanking.appcore.exceptions.BusinessException;
import com.panut.centralbanking.appcore.integrationtest.SeededDatabase;
import com.panut.centralbanking.transactionservice.core.TransactionStatus;
import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionProfileRepository;
import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class TransactionAuthorizationPipelineValidator {
    private final TransactionRepository transactionRepository;

    public TransactionAuthorizationPipelineValidator(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }



    private final TransactionStatus settledStatus = TransactionStatus.POSTED;

    public Mono<Void> isReversingTransactionCleared(final String transactionId) {
        return transactionRepository
                .getSettledTransaction(transactionId, settledStatus)
                .switchIfEmpty(Mono.error(
                        () -> new BusinessException("A reversed transaction required its target transaction " +
                                "to be settled first.")
                ))
                .then();
    }

}
