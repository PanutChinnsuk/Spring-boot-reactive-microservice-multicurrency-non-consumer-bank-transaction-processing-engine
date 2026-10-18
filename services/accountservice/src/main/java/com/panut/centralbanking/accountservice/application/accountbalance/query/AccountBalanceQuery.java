package com.panut.centralbanking.accountservice.application.accountbalance.query;

import com.panut.centralbanking.accountservice.infrastructure.model.AccountBalance;
import com.panut.centralbanking.accountservice.infrastructure.repository.AccountBalanceRepository;
import com.panut.centralbanking.appcore.exceptions.NotFound;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class AccountBalanceQuery {
    private final AccountBalanceRepository accountBalanceRepository;

    public AccountBalanceQuery(AccountBalanceRepository accountBalanceRepository) {
        this.accountBalanceRepository = accountBalanceRepository;
    }



    public Mono<AccountBalance> findForBalanceChange(final String accountId) {
        return accountBalanceRepository.findForBalanceChange(accountId)
                .switchIfEmpty(Mono.error(() -> new NotFound(
                        "Account does not have account balance."
                )));
    }
}
