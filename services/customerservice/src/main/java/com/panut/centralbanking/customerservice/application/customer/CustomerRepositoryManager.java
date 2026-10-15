package com.panut.centralbanking.customerservice.application.customer;

import com.panut.centralbanking.customerservice.infrastructure.model.Customer;
import com.panut.centralbanking.customerservice.infrastructure.repository.CustomerRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CustomerRepositoryManager {
    private final CustomerRepository customerRepository;

    public CustomerRepositoryManager(final CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }


    public Mono<Customer> save(Customer customer) {
        return customerRepository.save(customer);
    }
}
