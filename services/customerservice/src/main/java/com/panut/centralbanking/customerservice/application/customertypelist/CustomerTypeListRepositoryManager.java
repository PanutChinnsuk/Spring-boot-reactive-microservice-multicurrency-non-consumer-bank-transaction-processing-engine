package com.panut.centralbanking.customerservice.application.customertypelist;

import com.panut.centralbanking.customerservice.infrastructure.model.CustomerTypeList;
import com.panut.centralbanking.customerservice.infrastructure.repository.CustomerTypeListRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CustomerTypeListRepositoryManager {
    private final CustomerTypeListRepository customerTypeListRepository;

    public CustomerTypeListRepositoryManager(CustomerTypeListRepository customerTypeListRepository) {
        this.customerTypeListRepository = customerTypeListRepository;
    }

    public Mono<CustomerTypeList> save(CustomerTypeList customerTypeList) {
        return customerTypeListRepository.save(customerTypeList);
    }
}
