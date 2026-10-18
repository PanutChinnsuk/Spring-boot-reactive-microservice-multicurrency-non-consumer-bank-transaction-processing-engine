package com.panut.centralbanking.accountservice.infrastructure.repository;

import com.panut.centralbanking.accountservice.infrastructure.model.Account;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.Set;

public interface AccountRepository
        extends ReactiveCrudRepository<Account, String> {


    @Query("""
            SELECT a.id FROM accounts a
            WHERE a.id IN (:ids) FOR UPDATE
            """)
    Flux<String> findTransactionAccounts(@Param("ids")Set<String> accountIds);

}
