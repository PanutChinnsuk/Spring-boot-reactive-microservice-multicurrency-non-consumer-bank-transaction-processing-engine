package com.panut.centralbanking.customerservice.application.customertype;

import com.panut.centralbanking.customerservice.infrastructure.model.CustomerType;
import com.panut.centralbanking.customerservice.infrastructure.repository.CustomerTypeRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CustomerTypeRepositoryManager {
    private final CustomerTypeRepository customerTypeRepository;

    public CustomerTypeRepositoryManager(CustomerTypeRepository customerTypeRepository) {
        this.customerTypeRepository = customerTypeRepository;
    }


    public Mono<CustomerType> save(final CustomerType customerType) {
        return customerTypeRepository.save(customerType);
    }
}
