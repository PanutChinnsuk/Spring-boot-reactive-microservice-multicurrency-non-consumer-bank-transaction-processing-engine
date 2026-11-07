package com.panut.centralbanking.accountservice.application.accounts.accountoverdraft;

import com.panut.centralbanking.accountservice.infrastructure.model.AccountOverdraft;
import com.panut.centralbanking.accountservice.infrastructure.repository.AccountOverdraftRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class AccountOverdraftRepositoryManager {
    private final AccountOverdraftRepository accountOverdraftRepository;

    public AccountOverdraftRepositoryManager(AccountOverdraftRepository accountOverdraftRepository) {
        this.accountOverdraftRepository = accountOverdraftRepository;
    }


    public Mono<AccountOverdraft> save(final AccountOverdraft accountOverdraft) {
        return this.accountOverdraftRepository.save(accountOverdraft);
    }
}
