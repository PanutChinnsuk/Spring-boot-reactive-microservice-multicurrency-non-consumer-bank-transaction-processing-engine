package com.panut.centralbanking.accountservice.application.account;

import com.panut.centralbanking.accountservice.infrastructure.model.Account;
import com.panut.centralbanking.accountservice.infrastructure.repository.AccountRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class AccountRepositoryManager {
    private final AccountRepository accountRepository;

    public AccountRepositoryManager(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    public Mono<Account> save(final Account account) {
        return this.accountRepository.save(account);
    }
}
