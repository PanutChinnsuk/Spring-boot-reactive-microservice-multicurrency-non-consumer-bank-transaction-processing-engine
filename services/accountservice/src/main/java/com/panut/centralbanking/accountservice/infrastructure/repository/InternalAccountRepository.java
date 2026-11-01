package com.panut.centralbanking.accountservice.infrastructure.repository;


import com.panut.centralbanking.accountservice.core.InternalAccountType;
import com.panut.centralbanking.accountservice.core.InternalBranch;
import com.panut.centralbanking.accountservice.infrastructure.model.InternalAccount;
import com.panut.centralbanking.accountservice.infrastructure.repository.queryresult.AccountInfoForTransactionQueryResult;
import com.panut.centralbanking.accountservice.infrastructure.repository.queryresult.InternalAccountQueryResult;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface InternalAccountRepository
        extends ReactiveCrudRepository<InternalAccount, String> {


    @Query("""
            SELECT a.id FROM internal_accounts a
            WHERE
            (a.account_id = :accountId AND a.internal_account_branch_id = :branchId)
            OR
            (a.internal_account_branch_id = :branchId AND 
            a.internal_account_type = :internalAccountType)
            """)
    Mono<String> alreadyExists(@Param("accountId") String accountId,
                               @Param("branchId") String branchId,
                               @Param("internalAccountType")
                               InternalAccountType internalAccountType);

    @Query("""
            SELECT a.account_id, a.internal_account_type FROM internal_accounts a
            INNER JOIN internal_account_branches b
            ON b.branch = :currentBranch AND
            b.id = a.internal_account_branch_id
            WHERE a.internal_account_type IN (:internalAccountTypes)
            """)
    Flux<InternalAccountQueryResult> getInternalAccounts(
            @Param("currentBranch")InternalBranch currentBranch,
            @Param("internalAccountTypes")
            Set<InternalAccountType> internalAccountTypes);


}
