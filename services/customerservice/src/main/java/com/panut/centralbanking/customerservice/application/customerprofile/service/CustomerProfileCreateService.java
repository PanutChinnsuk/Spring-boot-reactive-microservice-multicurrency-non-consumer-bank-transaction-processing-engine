package com.panut.centralbanking.customerservice.application.customerprofile.service;

import com.panut.centralbanking.customerservice.api.data.CustomerProfileCreateData;
import com.panut.centralbanking.customerservice.application.customerprofile.CustomerProfileRepositoryManager;
import com.panut.centralbanking.customerservice.infrastructure.model.CustomerProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class CustomerProfileCreateService {
    private final CustomerProfileRepositoryManager customerProfileRepositoryManager;

    public CustomerProfileCreateService(CustomerProfileRepositoryManager customerProfileRepositoryManager) {
        this.customerProfileRepositoryManager = customerProfileRepositoryManager;
    }


    public Mono<CustomerProfile> createCustomerProfile(CustomerProfileCreateData data,
                                                       String customerId) {
        return customerProfileRepositoryManager
                .save(CustomerProfile.builder()
                        .customerId(customerId)
                        .firstname(data.getFirstname())
                        .lastname(data.getLastname())
                        .age(data.getAge())
                        .address(data.getAddress())
                        .gender(data.getGender())
                        .citizenId(data.getCitizenId())
                        .build())
                .doOnSuccess(a -> log.info("Created customer profile."))
                .doOnError(error -> log.error("Failed to create" +
                        " customer profile: {}", error.getLocalizedMessage()));
    }
}
