package com.panut.centralbanking.customerservice.application.customertype.service;

import com.panut.centralbanking.customerservice.core.enums.CustomerTypeEnum;
import com.panut.centralbanking.customerservice.infrastructure.model.CustomerType;
import reactor.core.publisher.Mono;

public interface CustomerTypeCreateService {
    Mono<CustomerType> addCustomerType(String customerId,
                                       CustomerTypeEnum adding);

    Mono<CustomerType> addDefault(String customerId);
}
