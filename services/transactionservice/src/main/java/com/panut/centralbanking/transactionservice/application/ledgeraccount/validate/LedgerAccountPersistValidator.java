package com.panut.centralbanking.transactionservice.application.ledgeraccount.validate;

import com.panut.centralbanking.appcore.exceptions.Conflicted;
import com.panut.centralbanking.transactionservice.infrastructure.repository.LedgerAccountRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class LedgerAccountPersistValidator {
    private final LedgerAccountRepository ledgerAccountRepository;

    public LedgerAccountPersistValidator(LedgerAccountRepository ledgerAccountRepository) {
        this.ledgerAccountRepository = ledgerAccountRepository;
    }


    public Mono<String> validateDuplicates(final String code) {
        return ledgerAccountRepository.existsByCode(code)
                .flatMap(exist -> {
                    if (exist)
                        return Mono.error(
                                () -> new Conflicted("Ledger account already exist."));

                    return Mono.just(code);
                });
    }
}
