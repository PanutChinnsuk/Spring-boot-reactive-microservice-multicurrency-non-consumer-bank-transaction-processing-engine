package com.panut.centralbanking.accountservice.application.accounts.accountoverdraft.query;

import com.panut.centralbanking.accountservice.infrastructure.model.AccountOverdraft;
import com.panut.centralbanking.accountservice.infrastructure.repository.AccountOverdraftRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class AccountOverdraftQuery {
    private final AccountOverdraftRepository repository;

    public AccountOverdraftQuery(AccountOverdraftRepository repository) {
        this.repository = repository;
    }


    public Mono<List<AccountOverdraft>>
    findAccountOverdrafts(final List<String> accountOverdraftIds) {
        return Mono.defer(() -> {
            if (accountOverdraftIds.isEmpty()) {
                log.info("accountOverdraftIds list is empty, returning empty list");
                return Mono.just(Collections.emptyList());
            }


            return repository.findForClearing(accountOverdraftIds)
                    .collectList();
        });

    }
}
