package com.panut.centralbanking.customerservice.application.customertypelistelement.query;

import com.panut.centralbanking.appcore.exceptions.NotFound;
import com.panut.centralbanking.customerservice.core.enums.CustomerTypeEnum;
import com.panut.centralbanking.customerservice.infrastructure.model.CustomerTypeListElement;
import com.panut.centralbanking.customerservice.infrastructure.repository.CustomerTypeListElementRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CustomerTypeListQuery {
    private final CustomerTypeListElementRepository customerTypeListElementRepository;

    public CustomerTypeListQuery(CustomerTypeListElementRepository customerTypeListElementRepository) {
        this.customerTypeListElementRepository = customerTypeListElementRepository;
    }


    public Mono<String> findIdGeneric(CustomerTypeEnum customerTypeEnum) {
        return customerTypeListElementRepository
                .findIdByCustomerTypeEnum(customerTypeEnum)
                .switchIfEmpty(Mono.error(() -> new NotFound(
                        "Customer type list table does not contain "
                                + customerTypeEnum
                )));

    }


}
