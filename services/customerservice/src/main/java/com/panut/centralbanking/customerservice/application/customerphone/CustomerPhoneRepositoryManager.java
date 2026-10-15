package com.panut.centralbanking.customerservice.application.customerphone;

import com.panut.centralbanking.customerservice.infrastructure.model.CustomerPhone;
import com.panut.centralbanking.customerservice.infrastructure.repository.CustomerPhoneRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CustomerPhoneRepositoryManager {
    private final CustomerPhoneRepository customerPhoneRepository;

    public CustomerPhoneRepositoryManager(CustomerPhoneRepository customerPhoneRepository) {
        this.customerPhoneRepository = customerPhoneRepository;
    }


    public Mono<CustomerPhone> save(final CustomerPhone customerPhone) {
        return customerPhoneRepository.save(customerPhone);
    }
}
