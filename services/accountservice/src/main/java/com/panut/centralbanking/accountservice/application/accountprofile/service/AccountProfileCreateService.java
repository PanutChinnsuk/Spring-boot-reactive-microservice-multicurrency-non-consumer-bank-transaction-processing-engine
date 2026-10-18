package com.panut.centralbanking.accountservice.application.accountprofile.service;

import com.panut.centralbanking.accountservice.api.data.AccountProfileCreateData;
import com.panut.centralbanking.accountservice.application.accountprofile.AccountProfileRepositoryManager;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class AccountProfileCreateService {
    private final AccountProfileRepositoryManager manager;

    public AccountProfileCreateService(AccountProfileRepositoryManager manager) {
        this.manager = manager;
    }


    public Mono<AccountProfile> createAccountProfile(final String accountId,
                                                     final AccountProfileCreateData data) {
        return manager.save(AccountProfile.builder()
                .accountId(accountId)
                .accountType(data.getAccountType())
                .accountCurrency(data.getCurrencyCode())
                .build())
                .doOnSuccess(a -> log.info("Created account profile."))
                .doOnError(error -> log.error("Failed to create account " +
                        "profile {}", error.getLocalizedMessage()));
    }

}
