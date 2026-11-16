package com.panut.centralbanking.transactionservice.testrepository;

import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionProfile;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collection;

public interface TransactionProfileTestRepository extends ReactiveCrudRepository<TransactionProfile, String> {


    @Modifying
    @Query("""
            UPDATE transaction_profiles tp SET tp.posted_at = NULL
            WHERE tp.id = :transactionId
            """)
    Mono<Void> setNullPosted(@Param("transactionId") String transactionId);

}
