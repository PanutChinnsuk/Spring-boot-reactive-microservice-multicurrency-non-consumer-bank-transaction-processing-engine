package com.panut.centralbanking.customerservice.application.customerprofile.query;

import com.panut.centralbanking.customerservice.infrastructure.repository.CustomerProfileRepository;
import org.springframework.stereotype.Component;

@Component
public class CustomerProfileQuery {
    private final CustomerProfileRepository customerProfileRepository;

    public CustomerProfileQuery(CustomerProfileRepository customerProfileRepository) {
        this.customerProfileRepository = customerProfileRepository;
    }
}
