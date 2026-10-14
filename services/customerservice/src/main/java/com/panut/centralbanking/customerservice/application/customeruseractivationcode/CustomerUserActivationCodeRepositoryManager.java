package com.panut.centralbanking.customerservice.application.customeruseractivationcode;

import com.panut.centralbanking.customerservice.infrastructure.model.CustomerUserActivationCode;
import com.panut.centralbanking.customerservice.infrastructure.repository.CustomerUserActivationCodeRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
public class CustomerUserActivationCodeRepositoryManager {
    private final CustomerUserActivationCodeRepository customerUserActivationCodeRepository;


    public CustomerUserActivationCodeRepositoryManager(CustomerUserActivationCodeRepository customerUserActivationCodeRepository) {
        this.customerUserActivationCodeRepository = customerUserActivationCodeRepository;
    }


    public Mono<CustomerUserActivationCode> save(CustomerUserActivationCode customerUserActivationCode) {
        return customerUserActivationCodeRepository.save(customerUserActivationCode);
    }


    public Mono<Void> revokeAllOfCustomer(String customerId) {
        return customerUserActivationCodeRepository
                .revokeAll(customerId, LocalDateTime.now());
    }



}
