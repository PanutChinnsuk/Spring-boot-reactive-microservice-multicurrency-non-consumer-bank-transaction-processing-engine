package com.panut.centralbanking.accountservice.application.accounts.accountbalance.query;

import com.panut.centralbanking.accountservice.infrastructure.model.AccountBalance;
import com.panut.centralbanking.accountservice.infrastructure.repository.AccountBalanceRepository;
import com.panut.centralbanking.appcore.exceptions.NotFound;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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


    public Mono<Map<String, AccountBalance>> findTransactionAccountBalances(final Collection<String> accountIds) {
      return queryTransactionAccountBalances(accountIds,
              accountBalanceRepository::findTransactionAccountBalances);
    }

    public Mono<Map<String, AccountBalance>> getTransactionAccountBalances(final Collection<String> accountIds) {
        return queryTransactionAccountBalances(accountIds,
                accountBalanceRepository::getTransactionAccountBalances);
    }


    private Mono<Map<String, AccountBalance>> queryTransactionAccountBalances
            (final Collection<String> accountIds,
             final Function<Collection<String>, Flux<AccountBalance>> queryFunc) {
        return queryFunc.apply(accountIds)
                .collectList()
                .map(list -> {
                    return list.stream().
                            collect(Collectors.toMap(AccountBalance::getAccountId,
                                    Function.identity()));
                });
    }
}
