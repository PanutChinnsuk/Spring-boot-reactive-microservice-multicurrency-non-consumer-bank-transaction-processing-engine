package com.panut.centralbanking.accountservice.application.internalaccounts.accounts;

import com.panut.centralbanking.accountservice.infrastructure.model.InternalAccount;
import com.panut.centralbanking.accountservice.infrastructure.repository.InternalAccountRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class InternalAccountRepositoryManager {
    private final InternalAccountRepository internalAccountRepository;

    public InternalAccountRepositoryManager(InternalAccountRepository internalAccountRepository) {
        this.internalAccountRepository = internalAccountRepository;
    }



    public Mono<InternalAccount> save(final InternalAccount internalAccount) {
        return internalAccountRepository.save(internalAccount);
    }

    public Mono<Void> deleteById(final String accountId) {
        return internalAccountRepository.deleteById(accountId);
    }
}
