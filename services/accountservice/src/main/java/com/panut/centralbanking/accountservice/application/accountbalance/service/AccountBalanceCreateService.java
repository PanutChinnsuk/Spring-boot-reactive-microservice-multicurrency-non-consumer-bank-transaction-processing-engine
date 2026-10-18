package com.panut.centralbanking.accountservice.application.accountbalance.service;

import com.panut.centralbanking.accountservice.api.data.AccountBalanceCreateData;
import com.panut.centralbanking.accountservice.application.accountbalance.AccountBalanceRepositoryManager;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountBalance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class AccountBalanceCreateService {
    private final AccountBalanceRepositoryManager manager;

    public AccountBalanceCreateService(AccountBalanceRepositoryManager manager) {
        this.manager = manager;
    }


    public Mono<AccountBalance> createAccountBalance(final String accountId,
                                                     final AccountBalanceCreateData data) {
        return manager.save(AccountBalance.builder()
                .accountId(accountId)
                .balance(data.getDefaultSeededBalance())
                .build())
                .doOnSuccess(a -> log.info("Created account balance."))
                .doOnError(error -> log.error("Failed to create account " +
                        "balance {}", error.getLocalizedMessage()));
    }
}
