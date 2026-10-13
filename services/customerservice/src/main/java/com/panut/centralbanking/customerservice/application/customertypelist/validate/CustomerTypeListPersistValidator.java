package com.panut.centralbanking.customerservice.application.customertypelist.validate;

import com.panut.centralbanking.appcore.exceptions.Conflicted;
import com.panut.centralbanking.customerservice.core.enums.CustomerTypeEnum;
import com.panut.centralbanking.customerservice.infrastructure.repository.CustomerTypeListRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CustomerTypeListPersistValidator {
    private final CustomerTypeListRepository customerTypeListRepository;

    public CustomerTypeListPersistValidator(CustomerTypeListRepository customerTypeListRepository) {
        this.customerTypeListRepository = customerTypeListRepository;
    }


    public Mono<Void> validateDuplicates(CustomerTypeEnum customerTypeEnum) {
        return customerTypeListRepository.existsByCustomerTypeEnum(customerTypeEnum)
                .flatMap(exist -> {
                    if (exist) return Mono.error(()
                            -> new Conflicted("Customer type list table already" +
                            " contain " + customerTypeEnum));

                    return Mono.empty();
                });
    }
}
