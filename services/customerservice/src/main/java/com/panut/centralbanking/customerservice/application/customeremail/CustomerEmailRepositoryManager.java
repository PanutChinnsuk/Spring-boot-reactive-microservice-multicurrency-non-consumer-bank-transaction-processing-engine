package com.panut.centralbanking.customerservice.application.customeremail;

import com.panut.centralbanking.customerservice.infrastructure.model.CustomerEmail;
import com.panut.centralbanking.customerservice.infrastructure.repository.CustomerEmailRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CustomerEmailRepositoryManager {
    private final CustomerEmailRepository customerEmailRepository;

    public CustomerEmailRepositoryManager(CustomerEmailRepository customerEmailRepository) {
        this.customerEmailRepository = customerEmailRepository;
    }


    public Mono<CustomerEmail> save(final CustomerEmail customerEmail) {
        return customerEmailRepository.save(customerEmail);
    }
}
