package com.panut.centralbanking.customerservice.application.customeruseractivationcode.service;

import com.panut.centralbanking.appcore.common.response.CommonResult;
import com.panut.centralbanking.customerservice.infrastructure.model.CustomerUserActivationCode;
import reactor.core.publisher.Mono;

public interface CustomerUserActivationCodeService {
    Mono<CustomerUserActivationCode> createCode(String customerId);
    Mono<CommonResult> renewCode(String customerId);
    Mono<CommonResult> revoke(String customerId);
    Mono<CommonResult> consumeValidCode(String customerId,
                                               String code,
                                               String userId);
}
