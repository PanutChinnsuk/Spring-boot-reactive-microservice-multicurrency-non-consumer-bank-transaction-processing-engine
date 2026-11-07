package com.panut.centralbanking.accountservice.application.accounts.accountprofile;

import com.panut.centralbanking.accountservice.infrastructure.model.AccountProfile;
import com.panut.centralbanking.accountservice.infrastructure.repository.AccountProfileRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class AccountProfileRepositoryManager {
    private final AccountProfileRepository accountProfileRepository;

    public AccountProfileRepositoryManager(AccountProfileRepository accountProfileRepository) {
        this.accountProfileRepository = accountProfileRepository;
    }


    public Mono<AccountProfile> save(final AccountProfile accountProfile) {
        return this.accountProfileRepository.save(accountProfile);
    }
}
