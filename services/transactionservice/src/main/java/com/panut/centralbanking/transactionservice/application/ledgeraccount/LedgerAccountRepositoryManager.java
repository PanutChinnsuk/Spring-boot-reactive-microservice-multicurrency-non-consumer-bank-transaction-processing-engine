package com.panut.centralbanking.transactionservice.application.ledgeraccount;

import com.panut.centralbanking.transactionservice.infrastructure.model.LedgerAccount;
import com.panut.centralbanking.transactionservice.infrastructure.repository.LedgerAccountRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class LedgerAccountRepositoryManager {
    private final LedgerAccountRepository ledgerAccountRepository;

    public LedgerAccountRepositoryManager(LedgerAccountRepository ledgerAccountRepository) {
        this.ledgerAccountRepository = ledgerAccountRepository;
    }


    public Mono<LedgerAccount> save(final LedgerAccount ledgerAccount) {
        return this.ledgerAccountRepository.save(ledgerAccount);
    }
}
