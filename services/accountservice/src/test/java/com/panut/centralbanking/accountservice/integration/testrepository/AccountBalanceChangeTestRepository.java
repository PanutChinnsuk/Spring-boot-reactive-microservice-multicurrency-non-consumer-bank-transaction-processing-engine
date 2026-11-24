package com.panut.centralbanking.accountservice.integration.testrepository;

import com.panut.centralbanking.accountservice.infrastructure.model.AccountBalanceChange;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.Collection;

public interface AccountBalanceChangeTestRepository
        extends ReactiveCrudRepository<AccountBalanceChange, String> {

    Mono<Void> deleteByAccountIdIn(Collection<String> accountIds);
}
