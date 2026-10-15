package com.panut.centralbanking.customerservice.application.customertypelistelement.validate;

import com.panut.centralbanking.appcore.exceptions.Conflicted;
import com.panut.centralbanking.customerservice.core.enums.CustomerTypeEnum;
import com.panut.centralbanking.customerservice.infrastructure.repository.CustomerTypeListElementRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CustomerTypeListPersistValidator {
    private final CustomerTypeListElementRepository customerTypeListElementRepository;

    public CustomerTypeListPersistValidator(CustomerTypeListElementRepository customerTypeListElementRepository) {
        this.customerTypeListElementRepository = customerTypeListElementRepository;
    }


    public Mono<Void> validateDuplicates(CustomerTypeEnum customerTypeEnum) {
        return customerTypeListElementRepository.existsByCustomerTypeEnum(customerTypeEnum)
                .flatMap(exist -> {
                    if (exist) return Mono.error(()
                            -> new Conflicted("Customer type list table already" +
                            " contain " + customerTypeEnum));

                    return Mono.empty();
                });
    }
}
