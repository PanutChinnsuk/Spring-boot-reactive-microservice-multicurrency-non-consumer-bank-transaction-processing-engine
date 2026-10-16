package com.panut.centralbanking.customerservice.infrastructure.repository.testrepository;

import com.panut.centralbanking.customerservice.infrastructure.model.CustomerProfile;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface CustomerProfileTestRepository extends ReactiveCrudRepository
<CustomerProfile, String> {

    @Query("""
            SELECT cp.id FROM customer_profiles cp
            WHERE cp.firstname = :firstname AND
            cp.lastname = :lastname
            LIMIT 1
            """)
    Mono<String> getCustomerId(@Param("firstname") String firstname,
                                          @Param("lastname") String lastname);

}
