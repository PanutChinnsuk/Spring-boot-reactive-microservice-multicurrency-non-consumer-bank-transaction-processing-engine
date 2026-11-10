package com.panut.centralbanking.accountservice.integration.testrepository;

import com.panut.centralbanking.accountservice.core.AccountOverdraftStatus;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountOverdraft;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Set;

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

    @Modifying
    @Query("""
            UPDATE account_overdrafts od SET
            od.paid = :toDefault,
            od.amount = :total
            WHERE od.id IN (:ids)
            """)
    Mono<Void> reset(@Param("ids")Set<String> ids,
                     @Param("toDefault")BigDecimal toDefault,
                     @Param("total") BigDecimal total);

    @Modifying
    @Query("""
            UPDATE account_overdrafts od SET
            od.status = :toStatus
            WHERE od.id IN (:ids)
            """)
    Mono<Void> resetStatus(@Param("ids")Set<String> ids,
                     @Param("toStatus") AccountOverdraftStatus toStatus);


    @Query("""
            SELECT od.* FROM account_overdrafts od
            WHERE od.id IN (:ids)
            """)
    Flux<AccountOverdraft> findByIds(@Param("ids")Collection<String> ids);
}
