package com.panut.centralbanking.accountservice.integration.testrepository;

import com.panut.centralbanking.accountservice.infrastructure.model.AccountBalance;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public interface AccountBalanceTestRepository
        extends ReactiveCrudRepository<AccountBalance, String> {


    @Modifying
    @Query("""
            UPDATE account_balances ab SET ab.balance = :defaultTo, 
            ab.reserve = 0, ab.ledger = :defaultTo, overdrafted = 0
            WHERE ab.id IN (:ids)
            """)
    Mono<Void> setDefaultBalance(@Param("defaultTo")BigDecimal defaultTo,
                                 @Param("ids") List<String> ids);

    @Modifying
    @Query("""
            UPDATE account_balances ab SET ab.balance = 0, 
            ab.reserve = 0, ab.ledger = :ledgerAmount, overdrafted = :defaultTo
            WHERE ab.id IN (:ids)
            """)
    Mono<Void> setDefaultOverdraft(@Param("defaultTo")BigDecimal defaultTo,
                                   @Param("ledgerAmount")BigDecimal ledgerAmount,
                                 @Param("ids") List<String> ids);




}
