package com.panut.centralbanking.customerservice.application.customer.query;

import com.panut.centralbanking.appcore.exceptions.NotFound;
import com.panut.centralbanking.customerservice.infrastructure.model.Customer;
import com.panut.centralbanking.customerservice.infrastructure.repository.CustomerRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CustomerQuery {
    private final CustomerRepository customerRepository;

    public CustomerQuery(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }


    public Mono<Customer> findGeneric(String customerId) {
        return customerRepository.findById(customerId)
                .switchIfEmpty(Mono.error(() -> new NotFound(
                        "Customer table does not contain customer id of " + customerId
                )));
    }

}
