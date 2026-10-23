package com.panut.centralbanking.accountservice.application.account.query;

import com.panut.centralbanking.accountservice.api.data.BalanceChangeData;
import com.panut.centralbanking.accountservice.application.account.query.result.TransactionAccounts;
import com.panut.centralbanking.accountservice.core.AccountStatus;
import com.panut.centralbanking.accountservice.infrastructure.model.Account;
import com.panut.centralbanking.accountservice.infrastructure.repository.AccountRepository;
import com.panut.centralbanking.accountservice.infrastructure.repository.queryresult.AccountsInfoForTransactionQueryResult;
import com.panut.centralbanking.appcore.exceptions.BusinessException;
import com.panut.centralbanking.appcore.exceptions.NotFound;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
//self reminder ->> SEPERATE FINDS/GETS TO DIFFERENT CLASSES ACCORDING TO ITS PLACE
//IF X SERVICES SCALED
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

    public Mono<List<String>> findStateAccounts(final Set<String> accountIds,
                                                final AccountStatus expectedStatus) {
        return accountRepository.findStateAccounts(accountIds, expectedStatus)
                .collectList()
                .flatMap(list -> {
                    if (list.size() != accountIds.size())
                        return Mono.<List<String>>error(
                                () -> new BusinessException("One or more accounts that" +
                                        " has expected account status of " + expectedStatus
                                + " is missing for current operation."));

                    return Mono.just(list);
                });
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

    public Mono<List<AccountsInfoForTransactionQueryResult>> getAccountsInfoForTransaction(
            final Set<String> accountIds) {
        return accountRepository
                .getAccountsInfoForTransaction(accountIds)
                .collectList();
    }
}
