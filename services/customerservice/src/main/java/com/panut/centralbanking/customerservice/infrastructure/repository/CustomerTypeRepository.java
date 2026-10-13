package com.panut.centralbanking.customerservice.infrastructure.repository;

import com.panut.centralbanking.customerservice.core.enums.CustomerTypeEnum;
import com.panut.centralbanking.customerservice.infrastructure.model.CustomerPhone;
import com.panut.centralbanking.customerservice.infrastructure.model.CustomerType;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface CustomerTypeRepository extends ReactiveCrudRepository
<CustomerType, String> {

    @Query("""
            SELECT 1 FROM customer_types ct
            INNER JOIN customer_type_list ctl
            ON ctl.type_enum = :addingTypeEnum AND
            ctl.id = ct.customer_type_list_id
            WHERE ct.customer_id = :customerId
            """)
    Mono<CustomerType> alreadyExists(@Param("addingTypeEnum") CustomerTypeEnum adding,
                                     @Param("customerId") String customerId);

}
