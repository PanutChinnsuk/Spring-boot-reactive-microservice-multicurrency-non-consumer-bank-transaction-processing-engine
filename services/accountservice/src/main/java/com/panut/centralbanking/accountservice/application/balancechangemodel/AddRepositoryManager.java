package com.panut.centralbanking.accountservice.application.balancechangemodel;

import com.panut.centralbanking.accountservice.infrastructure.model.impact.AccountBalanceAdd;
import com.panut.centralbanking.accountservice.infrastructure.repository.AccountBalanceAddRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class AddRepositoryManager {
    private AccountBalanceAddRepository accountBalanceAddRepository;

    public AddRepositoryManager(AccountBalanceAddRepository accountBalanceAddRepository) {
        this.accountBalanceAddRepository = accountBalanceAddRepository;
    }

    public Mono<AccountBalanceAdd> save(final AccountBalanceAdd accountBalanceAdd) {
        return this.accountBalanceAddRepository.save(accountBalanceAdd);
    }
}
