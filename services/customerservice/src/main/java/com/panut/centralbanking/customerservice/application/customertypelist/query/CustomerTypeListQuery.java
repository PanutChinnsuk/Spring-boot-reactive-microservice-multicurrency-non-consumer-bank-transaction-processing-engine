package com.panut.centralbanking.customerservice.application.customertypelist.query;

import com.panut.centralbanking.appcore.exceptions.NotFound;
import com.panut.centralbanking.customerservice.core.enums.CustomerTypeEnum;
import com.panut.centralbanking.customerservice.infrastructure.model.CustomerTypeList;
import com.panut.centralbanking.customerservice.infrastructure.repository.CustomerTypeListRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CustomerTypeListQuery {
    private final CustomerTypeListRepository customerTypeListRepository;

    public CustomerTypeListQuery(CustomerTypeListRepository customerTypeListRepository) {
        this.customerTypeListRepository = customerTypeListRepository;
    }


    public Mono<CustomerTypeList> findGeneric(CustomerTypeEnum customerTypeEnum) {
        return customerTypeListRepository
                .findByCustomerTypeEnum(customerTypeEnum)
                .switchIfEmpty(Mono.error(() -> new NotFound(
                        "Customer type list table does not contain "
                                + customerTypeEnum
                )));

    }


}
