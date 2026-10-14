package com.panut.centralbanking.customerservice.infrastructure.repository;

import com.panut.centralbanking.customerservice.infrastructure.model.CustomerPhone;
import com.panut.centralbanking.customerservice.infrastructure.model.CustomerUserActivationCode;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface CustomerUserActivationCodeRepository extends ReactiveCrudRepository
<CustomerUserActivationCode, String> {


    @Query("""
            UPDATE customer_user_activation_codes c
            SET c.revoked = true
            WHERE c.customer_id = :customerId AND
            c.revoked = false AND
            c.expires_at <= :currentTimestamp
            """)
    Mono<Void> revokeAll(@Param("customerId")String customerId,
                         @Param("currentTimestamp")LocalDateTime currentTimestamp);

    Mono<CustomerUserActivationCode> findByCustomerIdAndCode(String customerId,
                                                             String code);

    @Query("""
            SELECT c.* FROM customer_user_activation_codes c
            WHERE c.revoked = false AND
            c.expires_at > :currentTimestamp AND
            c.code = :code AND
            c.customer_id = :customerId FOR UPDATE
            """)
    Mono<CustomerUserActivationCode> findValid(@Param("customerId") String customerId,
                                               @Param("code") String code,
                                               @Param("currentTimestamp") LocalDateTime currentTimestamp);

}
