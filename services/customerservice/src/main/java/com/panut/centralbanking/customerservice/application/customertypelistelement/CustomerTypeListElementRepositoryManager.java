package com.panut.centralbanking.customerservice.application.customertypelistelement;

import com.panut.centralbanking.customerservice.infrastructure.model.CustomerTypeListElement;
import com.panut.centralbanking.customerservice.infrastructure.repository.CustomerTypeListElementRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CustomerTypeListElementRepositoryManager {
    private final CustomerTypeListElementRepository customerTypeListElementRepository;

    public CustomerTypeListElementRepositoryManager(CustomerTypeListElementRepository customerTypeListElementRepository) {
        this.customerTypeListElementRepository = customerTypeListElementRepository;
    }

    public Mono<CustomerTypeListElement> save(final CustomerTypeListElement customerTypeListElement) {
        return customerTypeListElementRepository.save(customerTypeListElement);
    }
}
