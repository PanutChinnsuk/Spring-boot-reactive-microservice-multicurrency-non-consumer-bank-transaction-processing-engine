package com.panut.centralbanking.customerservice.infrastructure.repository;

import com.panut.centralbanking.customerservice.core.enums.CustomerTypeEnum;
import com.panut.centralbanking.customerservice.infrastructure.model.CustomerPhone;
import com.panut.centralbanking.customerservice.infrastructure.model.CustomerTypeList;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface CustomerTypeListRepository extends ReactiveCrudRepository
<CustomerTypeList, String> {


    Mono<Boolean> existsByCustomerTypeEnum(CustomerTypeEnum customerTypeEnum);

    Mono<CustomerTypeList> findByCustomerTypeEnum(CustomerTypeEnum customerTypeEnum);
}
