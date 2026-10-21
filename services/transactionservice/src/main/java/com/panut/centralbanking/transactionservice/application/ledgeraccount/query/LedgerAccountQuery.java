package com.panut.centralbanking.transactionservice.application.ledgeraccount.query;

import com.panut.centralbanking.appcore.exceptions.NotFound;
import com.panut.centralbanking.transactionservice.infrastructure.repository.LedgerAccountRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class LedgerAccountQuery {
    private final LedgerAccountRepository ledgerAccountRepository;

    public LedgerAccountQuery(LedgerAccountRepository ledgerAccountRepository) {
        this.ledgerAccountRepository = ledgerAccountRepository;
    }


    public Mono<String> findIdGeneric(final String code) {
        return ledgerAccountRepository.getIdByCode(code)
                .switchIfEmpty(Mono.error(
                        () -> new NotFound("Ledger account table does not contain ledger account " +
                                "with code " + code)
                ));
    }

    public Mono<String> findExistNotThrow() {
        return ledgerAccountRepository.getIdExists();
    }
}
