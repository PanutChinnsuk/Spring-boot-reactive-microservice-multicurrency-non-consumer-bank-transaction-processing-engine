package com.panut.centralbanking.accountservice.integration.testrepository;

import com.panut.centralbanking.accountservice.infrastructure.model.AccountOverdraft;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountOverdraftTestRepository
        extends ReactiveCrudRepository<AccountOverdraft, String> {

    Mono<Void> deleteByAccountId(String accountId);
}
