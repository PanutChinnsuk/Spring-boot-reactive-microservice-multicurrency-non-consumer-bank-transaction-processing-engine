package com.panut.centralbanking.customerservice.application.customertype.query;

import com.panut.centralbanking.customerservice.infrastructure.repository.CustomerTypeRepository;
import org.springframework.stereotype.Component;

@Component
public class CustomerTypeQuery {
    private final CustomerTypeRepository customerTypeRepository;

    public CustomerTypeQuery(CustomerTypeRepository customerTypeRepository) {
        this.customerTypeRepository = customerTypeRepository;
    }
}
