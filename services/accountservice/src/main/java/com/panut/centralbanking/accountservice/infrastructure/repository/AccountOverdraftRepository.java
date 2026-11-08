package com.panut.centralbanking.accountservice.infrastructure.repository;

import com.panut.centralbanking.accountservice.infrastructure.model.Account;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountOverdraft;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface AccountOverdraftRepository
        extends ReactiveCrudRepository<AccountOverdraft, String> {

    Flux<AccountOverdraft> findByAccountId(String accountId);
}
