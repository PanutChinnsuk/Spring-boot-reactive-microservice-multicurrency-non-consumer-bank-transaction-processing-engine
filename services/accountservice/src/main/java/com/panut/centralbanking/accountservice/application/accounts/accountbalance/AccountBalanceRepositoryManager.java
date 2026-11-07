package com.panut.centralbanking.accountservice.application.accountbalance;

import com.panut.centralbanking.accountservice.infrastructure.model.AccountBalance;
import com.panut.centralbanking.accountservice.infrastructure.repository.AccountBalanceRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

@Component
public class AccountBalanceRepositoryManager {
    private final AccountBalanceRepository accountBalanceRepository;

    public AccountBalanceRepositoryManager(AccountBalanceRepository accountBalanceRepository) {
        this.accountBalanceRepository = accountBalanceRepository;
    }


    public Mono<AccountBalance> save(final AccountBalance accountBalance) {
        return this.accountBalanceRepository.save(accountBalance);
    }


    public Mono<List<AccountBalance>> saveAll(final Collection<AccountBalance> accountBalances) {
        return this.accountBalanceRepository.saveAll(accountBalances)
                .collectList();
    }
}
