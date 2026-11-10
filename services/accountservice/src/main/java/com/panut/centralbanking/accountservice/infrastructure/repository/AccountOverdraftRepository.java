package com.panut.centralbanking.accountservice.infrastructure.repository;

import com.panut.centralbanking.accountservice.infrastructure.model.Account;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountOverdraft;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface AccountOverdraftRepository
        extends ReactiveCrudRepository<AccountOverdraft, String> {

    Flux<AccountOverdraft> findByAccountId(String accountId);


    @Query("""
            SELECT od.* FROM account_overdrafts od
            WHERE od.id IN (:accountOverdraftIds)
            FOR UPDATE
            """)
    Flux<AccountOverdraft> findForClearing(@Param("accountOverdraftIds")
                                           List<String> accountOverdraftIds);
}
