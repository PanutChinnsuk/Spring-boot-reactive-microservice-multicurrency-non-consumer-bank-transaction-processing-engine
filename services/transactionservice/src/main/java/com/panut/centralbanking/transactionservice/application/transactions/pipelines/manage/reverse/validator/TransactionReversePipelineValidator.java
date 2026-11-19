package com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.reverse.validator;

import com.panut.centralbanking.appcore.exceptions.BusinessException;
import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionProfileRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class TransactionReversePipelineValidator {
    private final TransactionProfileRepository profileRepository;

    public TransactionReversePipelineValidator(TransactionProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public Mono<Void> isAlreadyReversed(final String reversingId) {
        return profileRepository.getIdAlreadyReversed(reversingId)
                .flatMap(id -> Mono.error(
                        () -> new BusinessException("This reversing transaction id of " + reversingId +
                                 " has already been reversed.")
                )).then();
    }
}
