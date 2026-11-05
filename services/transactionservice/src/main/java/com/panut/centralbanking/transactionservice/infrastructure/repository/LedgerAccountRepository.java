package com.panut.centralbanking.transactionservice.infrastructure.repository;

import com.panut.centralbanking.transactionservice.core.LedgerType;
import com.panut.centralbanking.transactionservice.infrastructure.model.LedgerAccount;
import com.panut.centralbanking.transactionservice.infrastructure.repository.querydto.LedgerAccountMapQueryResult;
import com.panut.centralbanking.transactionservice.infrastructure.repository.querydto.LedgerTypeMapQueryResult;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

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


    @Query("""
            SELECT l.id, l.code FROM ledger_accounts l
            WHERE l.code IN (:codes) AND
            l.ledger_type IN (:ledgerTypes)
            """)
    Flux<LedgerAccountMapQueryResult> getLedgerAccountMapByCodes(@Param("codes")Set<String> codes,
                                                                 @Param("ledgerTypes")
                                                                 Set<LedgerType> ledgerTypes);


    @Query("""
            SELECT l.id, l.ledger_type FROM ledger_accounts l
            INNER JOIN transaction_lines tl
            ON tl.ledger_account_id = l.id AND
            tl.transaction_id = :transactionId
            """)
    Flux<LedgerTypeMapQueryResult> getLedgerTypeMapByTransactionId(@Param("transactionId") String transactionId);

}
