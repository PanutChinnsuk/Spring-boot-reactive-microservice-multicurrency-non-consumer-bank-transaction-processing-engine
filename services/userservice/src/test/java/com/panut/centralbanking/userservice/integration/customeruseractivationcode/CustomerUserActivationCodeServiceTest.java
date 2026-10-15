package com.panut.centralbanking.userservice.integration.customeruseractivationcode;

import com.panut.centralbanking.appcore.common.response.CommonResult;
import com.panut.centralbanking.appcore.exceptions.BusinessException;
import com.panut.centralbanking.userservice.IntegrationDev;
import com.panut.centralbanking.userservice.api.service.customerservice.CustomerUserActivationCodeService;
import com.panut.centralbanking.userservice.api.service.customerservice.request.GetUserActivationCodeRequest;
import com.panut.centralbanking.userservice.api.service.customerservice.response.UserDefaultPasswordData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

public class CustomerUserActivationCodeServiceTest implements IntegrationDev {
    @Autowired
    private CustomerUserActivationCodeService service;



    @Test
    public void shouldReturnError() {
        UserDefaultPasswordData data = service.getCode(
                new GetUserActivationCodeRequest("customerId", "code")
        ).block();


    }

}
