package com.panut.centralbanking.accountservice.infrastructure.repository;

import com.panut.centralbanking.accountservice.infrastructure.model.AccountBalance;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface AccountBalanceRepository
        extends ReactiveCrudRepository<AccountBalance, String> {


    @Query("""
            SELECT ab.* FROM account_balances ab
            WHERE ab.id = :accountId LIMIT 1 FOR UPDATE
            """)
    Mono<AccountBalance> findForBalanceChange(@Param("accountId") String accountId);


    @Query("""
            SELECT ab.* FROM account_balances ab
            WHERE ab.id IN (:accountIds) FOR UPDATE
            """)
    Flux<AccountBalance> findTransactionAccountBalances
            (@Param("accountIds") Set<String> accountIds);
}
