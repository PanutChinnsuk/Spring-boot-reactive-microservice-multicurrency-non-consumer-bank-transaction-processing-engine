package com.panut.centralbanking.customerservice.api.controller.service;

import com.panut.centralbanking.appcore.common.response.CommonResult;
import com.panut.centralbanking.appcore.servicemaps.InternalServiceMapping;
import com.panut.centralbanking.appcore.servicemaps.map.CustomerService;
import com.panut.centralbanking.customerservice.api.request.ActivateUserRequest;
import com.panut.centralbanking.customerservice.application.customeruseractivationcode.service.CustomerUserActivationCodeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(InternalServiceMapping.API)
public class CustomerUserActivationCodeController {
    private final CustomerUserActivationCodeService customerUserActivationCodeService;

    public CustomerUserActivationCodeController(CustomerUserActivationCodeService customerUserActivationCodeService) {
        this.customerUserActivationCodeService = customerUserActivationCodeService;
    }


    @PostMapping(CustomerService.ACTIVATE_USER_METHOD)
    public Mono<CommonResult> activateUser(@RequestBody
                                           @Valid ActivateUserRequest r) {
        return customerUserActivationCodeService
                .consumeValidCode(r.getCustomerId(),
                        r.getCode(),
                        r.getUserId());
    }
}
