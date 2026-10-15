package com.panut.centralbanking.customerservice.infrastructure.repository;

import com.panut.centralbanking.customerservice.core.enums.CustomerTypeEnum;
import com.panut.centralbanking.customerservice.infrastructure.model.CustomerTypeListElement;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface CustomerTypeListElementRepository extends ReactiveCrudRepository
<CustomerTypeListElement, String> {


    Mono<Boolean> existsByCustomerTypeEnum(CustomerTypeEnum customerTypeEnum);


    @Query("""
            SELECT e.id FROM customer_type_list e
            WHERE e.type_enum = :customerTypeEnum
            """)
    Mono<String> findIdByCustomerTypeEnum(@Param("customerTypeEnum") CustomerTypeEnum customerTypeEnum);
}
