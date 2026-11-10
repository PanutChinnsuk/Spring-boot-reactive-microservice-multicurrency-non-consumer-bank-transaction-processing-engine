package com.panut.centralbanking.accountservice.integration.testrepository;

import com.panut.centralbanking.accountservice.infrastructure.model.AccountOverdraft;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountOverdraftTestRepository
        extends ReactiveCrudRepository<AccountOverdraft, String> {

    @Modifying
    @Query("""
            DELETE FROM account_overdrafts od 
            WHERE od.account_id = :accountId
            AND od.id NOT LIKE :excludedIdFlag
            """)
    Mono<Void> deleteByAccountId(@Param("accountId") String accountId,
                                 @Param("excludedIdFlag") String excludedIdFlag);
}
