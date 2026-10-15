package com.panut.centralbanking.customerservice.application.customeremail.service;

import com.panut.centralbanking.customerservice.api.data.CustomerEmailCreateData;
import com.panut.centralbanking.customerservice.application.customeremail.CustomerEmailRepositoryManager;
import com.panut.centralbanking.customerservice.infrastructure.model.CustomerEmail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class CustomerEmailCreateService {
    private final CustomerEmailRepositoryManager customerEmailRepositoryManager;

    public CustomerEmailCreateService(CustomerEmailRepositoryManager customerEmailRepositoryManager) {
        this.customerEmailRepositoryManager = customerEmailRepositoryManager;
    }


    public Mono<CustomerEmail> createDefaultCustomerEmail(final CustomerEmailCreateData data,
                                                          final String customerId) {
        return customerEmailRepositoryManager.save(
                CustomerEmail.builder()
                        .customerId(customerId)
                        .email(data.getEmail())
                        .build())
                .doOnSuccess(a -> log.info("Created customer email."))
                .doOnError(error -> log.error("Failed to create" +
                        " customer email: {}", error.getLocalizedMessage()));

    }
}
