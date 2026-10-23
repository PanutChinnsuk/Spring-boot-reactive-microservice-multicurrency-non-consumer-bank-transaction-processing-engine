package com.panut.centralbanking.accountservice.infrastructure.repository;

import com.panut.centralbanking.accountservice.core.AccountStatus;
import com.panut.centralbanking.accountservice.infrastructure.model.Account;
import com.panut.centralbanking.accountservice.infrastructure.repository.queryresult.AccountsInfoForTransactionQueryResult;
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

    @Query("""
            SELECT a.id, ap.account_currency, ap.status FROM accounts a
            INNER JOIN account_profiles ap
            ON ap.id = a.id
            WHERE a.id IN (:accountIds)
            FOR UPDATE
            """)
    Flux<AccountsInfoForTransactionQueryResult> getAccountsInfoForTransaction(
            @Param("accountIds") Set<String> accountIds);


    @Query("""
            SELECT a.id FROM accounts a
            WHERE a.account_id IN (:accountIds) AND
            a.status = :expectedStatus
            """)
    Flux<String> findStateAccounts(@Param("accountIds") Set<String> accountIds,
                                   @Param("expectedStatus")AccountStatus expectedStatus);

}
