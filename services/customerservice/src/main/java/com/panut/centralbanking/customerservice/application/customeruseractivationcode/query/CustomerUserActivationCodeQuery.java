package com.panut.centralbanking.customerservice.application.customeruseractivationcode.query;

import com.panut.centralbanking.customerservice.infrastructure.repository.CustomerUserActivationCodeRepository;
import org.springframework.stereotype.Component;

@Component
public class CustomerUserActivationCodeQuery {
    public CustomerUserActivationCodeQuery(CustomerUserActivationCodeRepository customerUserActivationCodeRepository) {
        this.customerUserActivationCodeRepository = customerUserActivationCodeRepository;
    }

    private final CustomerUserActivationCodeRepository customerUserActivationCodeRepository;


}
