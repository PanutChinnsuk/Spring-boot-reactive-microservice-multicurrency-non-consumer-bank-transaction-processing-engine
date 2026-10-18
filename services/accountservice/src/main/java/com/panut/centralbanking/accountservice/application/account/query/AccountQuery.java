package com.panut.centralbanking.accountservice.application.account.query;

import com.panut.centralbanking.accountservice.infrastructure.model.Account;
import com.panut.centralbanking.accountservice.infrastructure.repository.AccountRepository;
import com.panut.centralbanking.appcore.exceptions.NotFound;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class AccountQuery {
    private final AccountRepository accountRepository;

    public AccountQuery(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    private <T>Mono<T> error(final String accountId) {
        return Mono.error(() -> new NotFound("Account tables " +
                "does not contain account id of " + accountId));
    }

    public Mono<Account> findGeneric(final String accountId) {
        return this.accountRepository.findById(accountId)
                .switchIfEmpty(error(accountId));
    }
}
