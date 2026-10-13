package com.panut.centralbanking.customerservice.application.customerphone.service;


import com.panut.centralbanking.customerservice.api.data.CustomerPhoneCreateData;
import com.panut.centralbanking.customerservice.application.customerphone.CustomerPhoneRepositoryManager;
import com.panut.centralbanking.customerservice.infrastructure.model.CustomerPhone;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class CustomerPhoneCreateService {
    private final CustomerPhoneRepositoryManager customerPhoneRepositoryManager;

    public CustomerPhoneCreateService(CustomerPhoneRepositoryManager customerPhoneRepositoryManager) {
        this.customerPhoneRepositoryManager = customerPhoneRepositoryManager;
    }


    public Mono<CustomerPhone> createDefaultCustomerPhone(CustomerPhoneCreateData data,
                                                          String customerId) {
        return customerPhoneRepositoryManager.save(
                        CustomerPhone.builder()
                                .customerId(customerId)
                                .phone(data.getPhone())
                                .build())
                .doOnSuccess(a -> log.info("Created customer phone."))
                .doOnError(error -> log.error("Failed to create" +
                        " customer phone: {}", error.getLocalizedMessage()));

    }
}
