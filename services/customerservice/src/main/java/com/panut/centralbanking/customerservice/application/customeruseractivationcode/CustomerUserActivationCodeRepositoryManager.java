package com.panut.centralbanking.customerservice.application.customeruseractivationcode;

import com.panut.centralbanking.customerservice.infrastructure.repository.CustomerUserActivationCodeRepository;
import org.springframework.stereotype.Component;

@Component
public class CustomerUserActivationCodeRepositoryManager {
    public CustomerUserActivationCodeRepositoryManager(CustomerUserActivationCodeRepository customerUserActivationCodeRepository) {
        this.customerUserActivationCodeRepository = customerUserActivationCodeRepository;
    }

    private final CustomerUserActivationCodeRepository customerUserActivationCodeRepository;


}
