package com.panut.centralbanking.customerservice.application.customerprofile;

import com.panut.centralbanking.customerservice.infrastructure.model.CustomerProfile;
import com.panut.centralbanking.customerservice.infrastructure.repository.CustomerProfileRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CustomerProfileRepositoryManager {
    private final CustomerProfileRepository customerProfileRepository;

    public CustomerProfileRepositoryManager(CustomerProfileRepository customerProfileRepository) {
        this.customerProfileRepository = customerProfileRepository;
    }


    public Mono<CustomerProfile> save(CustomerProfile customerProfile) {
        return customerProfileRepository.save(customerProfile);
    }
}
