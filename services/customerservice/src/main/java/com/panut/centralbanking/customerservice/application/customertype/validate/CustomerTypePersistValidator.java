package com.panut.centralbanking.customerservice.application.customertype.validate;

import com.panut.centralbanking.appcore.exceptions.Conflicted;
import com.panut.centralbanking.customerservice.core.enums.CustomerTypeEnum;
import com.panut.centralbanking.customerservice.infrastructure.repository.CustomerTypeRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CustomerTypePersistValidator {
    private final CustomerTypeRepository customerTypeRepository;


    public CustomerTypePersistValidator(CustomerTypeRepository customerTypeRepository) {
        this.customerTypeRepository = customerTypeRepository;
    }


    public Mono<Void> validateDuplicates(CustomerTypeEnum adding,
                                         String customerId) {
        return customerTypeRepository
                .alreadyExists(adding, customerId)
                .flatMap(exist -> Mono.error(() ->
                        new Conflicted("Customer already have " + adding
                        + " customer type.")))
                .then();
    }
}
