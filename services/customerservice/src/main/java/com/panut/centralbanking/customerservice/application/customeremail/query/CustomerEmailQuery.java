package com.panut.centralbanking.customerservice.application.customeremail.query;

import com.panut.centralbanking.customerservice.infrastructure.repository.CustomerEmailRepository;
import org.springframework.stereotype.Component;

@Component
public class CustomerEmailQuery {
    private final CustomerEmailRepository customerEmailRepository;

    public CustomerEmailQuery(CustomerEmailRepository customerEmailRepository) {
        this.customerEmailRepository = customerEmailRepository;
    }
}
