package com.panut.centralbanking.accountservice.application.transactions.accountbalancechange;

import com.panut.centralbanking.accountservice.infrastructure.model.AccountBalanceChange;
import com.panut.centralbanking.accountservice.infrastructure.repository.AccountBalanceChangeRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class BalanceChangeRepositoryManager {
    private AccountBalanceChangeRepository accountBalanceChangeRepository;

    public BalanceChangeRepositoryManager(AccountBalanceChangeRepository accountBalanceChangeRepository) {
        this.accountBalanceChangeRepository = accountBalanceChangeRepository;
    }

    public Mono<AccountBalanceChange> save(final AccountBalanceChange accountBalanceChange) {
        return this.accountBalanceChangeRepository.save(accountBalanceChange);
    }

    public Mono<List<AccountBalanceChange>> saveAll(final List<AccountBalanceChange> changes) {
        return this.accountBalanceChangeRepository.saveAll(changes)
                .collectList();
    }
}
