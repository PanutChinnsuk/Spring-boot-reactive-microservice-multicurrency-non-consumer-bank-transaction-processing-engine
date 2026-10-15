package com.panut.centralbanking.customerservice.application.customertypelistelement.service;

import com.panut.centralbanking.customerservice.core.enums.CustomerTypeEnum;
import com.panut.centralbanking.customerservice.infrastructure.model.CustomerTypeListElement;
import reactor.core.publisher.Mono;

public interface CustomerTypeListService {
    Mono<CustomerTypeListElement> add(CustomerTypeEnum customerTypeEnum);
}
