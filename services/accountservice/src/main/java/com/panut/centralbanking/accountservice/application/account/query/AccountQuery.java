package com.panut.centralbanking.accountservice.application.account.query;

import com.panut.centralbanking.accountservice.api.data.BalanceChangeData;
import com.panut.centralbanking.accountservice.application.account.query.result.TransactionAccounts;
import com.panut.centralbanking.accountservice.infrastructure.model.Account;
import com.panut.centralbanking.accountservice.infrastructure.repository.AccountRepository;
import com.panut.centralbanking.appcore.exceptions.NotFound;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    public Mono<TransactionAccounts> findTransactionAccounts(final Set<String> transactionAccountIds) {
        return accountRepository
                .findTransactionAccounts(transactionAccountIds)
                .collectList()
                .map(list -> new HashSet<>(list))
                .flatMap(setIds -> {
                    if (!setIds.containsAll(transactionAccountIds))
                        return Mono.<TransactionAccounts>error(
                                () -> new NotFound("One or more accounts " +
                                        "in transaction are missing to perform " +
                                        "account balance changes.")
                        );

                    Map<String,String> accountMap = setIds.stream()
                            .collect(Collectors.toMap(
                                    key -> key,
                                    value -> value));

                    return Mono.just(new TransactionAccounts(transactionAccountIds,
                            accountMap));
                });


    }
}
