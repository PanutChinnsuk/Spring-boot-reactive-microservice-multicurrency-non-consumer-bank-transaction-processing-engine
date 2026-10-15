package com.panut.centralbanking.customerservice.application.customer.query;

import com.panut.centralbanking.appcore.exceptions.NotFound;
import com.panut.centralbanking.customerservice.infrastructure.model.Customer;
import com.panut.centralbanking.customerservice.infrastructure.repository.CustomerRepository;
import com.panut.centralbanking.customerservice.infrastructure.repository.queryresult.CustomerWithCustomerProfile;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CustomerQuery {
    private final CustomerRepository customerRepository;
    private final DatabaseClient databaseClient;

    public CustomerQuery(CustomerRepository customerRepository,
                         DatabaseClient databaseClient) {
        this.customerRepository = customerRepository;
        this.databaseClient = databaseClient;
    }

    private <T> Mono <T> error(final String customerId) {
        return Mono.error(() -> new NotFound(
                "Customer table does not contain customer id of " + customerId
        ));
    }


    public Mono<Customer> findGeneric(final String customerId) {
        return customerRepository.findById(customerId)
                .switchIfEmpty(error(customerId));
    }

    public Mono<String> findIdGeneric(final String customerId) {
        return customerRepository.findIdById(customerId)
                .switchIfEmpty(error(customerId));
    }


    public Mono<Void> findExistsThrow(final String customerId) {
        return customerRepository.existsById(customerId)
                .filter(Boolean::booleanValue)
                .switchIfEmpty(error(customerId))
                .then();
    }





}
