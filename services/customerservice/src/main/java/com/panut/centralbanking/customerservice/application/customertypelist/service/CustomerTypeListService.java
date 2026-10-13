package com.panut.centralbanking.customerservice.application.customertypelist.service;

import com.panut.centralbanking.customerservice.core.enums.CustomerTypeEnum;
import com.panut.centralbanking.customerservice.infrastructure.model.CustomerTypeList;
import reactor.core.publisher.Mono;

public interface CustomerTypeListService {
    Mono<CustomerTypeList> add(CustomerTypeEnum customerTypeEnum);
}
