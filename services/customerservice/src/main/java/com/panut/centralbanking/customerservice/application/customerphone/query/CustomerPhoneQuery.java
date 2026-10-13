package com.panut.centralbanking.customerservice.application.customerphone.query;

import com.panut.centralbanking.customerservice.infrastructure.repository.CustomerPhoneRepository;
import org.springframework.stereotype.Component;

@Component
public class CustomerPhoneQuery {
    private final CustomerPhoneRepository customerPhoneRepository;

    public CustomerPhoneQuery(CustomerPhoneRepository customerPhoneRepository) {
        this.customerPhoneRepository = customerPhoneRepository;
    }
}
