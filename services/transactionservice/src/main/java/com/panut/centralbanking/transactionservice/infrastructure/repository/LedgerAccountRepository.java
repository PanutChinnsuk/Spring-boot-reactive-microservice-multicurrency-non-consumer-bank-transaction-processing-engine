package com.panut.centralbanking.transactionservice.infrastructure.repository;

import com.panut.centralbanking.transactionservice.infrastructure.model.LedgerAccount;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface LedgerAccountRepository extends ReactiveCrudRepository<LedgerAccount, String> {


    @Query("""
            SELECT l.id FROM ledger_accounts l
            ORDER BY l.created_at DESC LIMIT 1 FOR UPDATE
            """)
    Mono<String> getIdExists();

    Mono<Boolean> existsByCode(String code);

    @Query("""
            SELECT l.id FROM ledger_accounts l 
            WHERE l.code = :code LIMIT 1
            """)
    Mono<String> getIdByCode(@Param("code") String code);



}
