package com.panut.centralbanking.customerservice.application.customeruseractivationcode.service;

import com.panut.centralbanking.customerservice.infrastructure.model.CustomerUserActivationCode;
import reactor.core.publisher.Mono;

public interface CustomerUserActivationCodeCreateService {
    Mono<CustomerUserActivationCode> createCode(String customerId);
}
