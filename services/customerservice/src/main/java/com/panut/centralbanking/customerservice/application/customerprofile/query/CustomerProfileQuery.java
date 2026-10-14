package com.panut.centralbanking.customerservice.application.customerprofile.query;

import com.panut.centralbanking.appcore.exceptions.NotFound;
import com.panut.centralbanking.customerservice.infrastructure.model.CustomerProfile;
import com.panut.centralbanking.customerservice.infrastructure.repository.CustomerProfileRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CustomerProfileQuery {
    private final CustomerProfileRepository customerProfileRepository;

    public CustomerProfileQuery(CustomerProfileRepository customerProfileRepository) {
        this.customerProfileRepository = customerProfileRepository;
    }



    public Mono<CustomerProfile> findGeneric(String customerId) {
        return customerProfileRepository.findById(customerId)
                .switchIfEmpty(Mono.error(() -> new NotFound("Customer profile table" +
                        " does not contain customer profile id of " + customerId)));
    }

}
