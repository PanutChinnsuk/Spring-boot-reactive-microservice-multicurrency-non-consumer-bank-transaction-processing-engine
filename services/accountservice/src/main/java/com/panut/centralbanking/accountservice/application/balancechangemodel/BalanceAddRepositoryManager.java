package com.panut.centralbanking.accountservice.application.balancechangemodel;

import com.panut.centralbanking.accountservice.infrastructure.model.impact.AccountBalanceAdd;
import com.panut.centralbanking.accountservice.infrastructure.repository.AccountBalanceAddRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class BalanceAddRepositoryManager {
    private AccountBalanceAddRepository accountBalanceAddRepository;

    public BalanceAddRepositoryManager(AccountBalanceAddRepository accountBalanceAddRepository) {
        this.accountBalanceAddRepository = accountBalanceAddRepository;
    }

    public Mono<AccountBalanceAdd> save(final AccountBalanceAdd accountBalanceAdd) {
        return this.accountBalanceAddRepository.save(accountBalanceAdd);
    }

    public Mono<List<AccountBalanceAdd>> saveAll(final List<AccountBalanceAdd> adds) {
        return this.accountBalanceAddRepository.saveAll(adds)
                .collectList();
    }
}
