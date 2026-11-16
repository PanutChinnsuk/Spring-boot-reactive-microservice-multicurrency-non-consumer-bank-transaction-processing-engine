package com.panut.centralbanking.transactionservice.infrastructure.repository;

import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionProfile;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collection;

public interface TransactionProfileRepository extends ReactiveCrudRepository<TransactionProfile, String> {


    @Query("""
            SELECT tp.* FROM transaction_profiles tp
            WHERE tp.id = :transactionId
            FOR UPDATE
            """)
    Mono<TransactionProfile> findTransactionProfileById(@Param("transactionId") String transactionId);


    @Modifying
    @Query("""
            UPDATE transaction_profiles tp 
            SET tp.posted_at = :currentTimestamp
            WHERE tp.id IN (:transactionIds)
            """)
    Mono<Void> setPosted(@Param("transactionIds") Collection<String> transactionIds,
                         @Param("currentTimestamp")LocalDateTime currentTimestamp);
}
