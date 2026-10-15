package com.panut.centralbanking.customerservice.infrastructure.repository;

import com.panut.centralbanking.customerservice.core.enums.CustomerTypeEnum;
import com.panut.centralbanking.customerservice.infrastructure.model.CustomerTypeListElement;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface CustomerTypeListElementRepository extends ReactiveCrudRepository
<CustomerTypeListElement, String> {


    Mono<Boolean> existsByCustomerTypeEnum(CustomerTypeEnum customerTypeEnum);

    Mono<CustomerTypeListElement> findByCustomerTypeEnum(CustomerTypeEnum customerTypeEnum);
}
