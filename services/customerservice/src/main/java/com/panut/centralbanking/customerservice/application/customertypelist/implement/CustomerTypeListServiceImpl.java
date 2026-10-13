package com.panut.centralbanking.customerservice.application.customertypelist.implement;

import com.panut.centralbanking.customerservice.application.customertypelist.CustomerTypeListRepositoryManager;
import com.panut.centralbanking.customerservice.application.customertypelist.validate.CustomerTypeListPersistValidator;
import com.panut.centralbanking.customerservice.core.enums.CustomerTypeEnum;
import com.panut.centralbanking.customerservice.infrastructure.model.CustomerTypeList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class CustomerTypeListServiceImpl {
    private final CustomerTypeListPersistValidator customerTypeListPersistValidator;
    private final CustomerTypeListRepositoryManager customerTypeListRepositoryManager;

    public CustomerTypeListServiceImpl(CustomerTypeListPersistValidator customerTypeListPersistValidator,
                                       CustomerTypeListRepositoryManager customerTypeListRepositoryManager) {
        this.customerTypeListPersistValidator = customerTypeListPersistValidator;
        this.customerTypeListRepositoryManager = customerTypeListRepositoryManager;
    }


    public Mono<CustomerTypeList> add(CustomerTypeEnum customerTypeEnum) {
        return customerTypeListPersistValidator
                .validateDuplicates(customerTypeEnum)
                .then(customerTypeListRepositoryManager.save(
                        CustomerTypeList.builder()
                                .customerTypeEnum(customerTypeEnum)
                                .build()))
                .doOnSuccess(a -> log.info("Created customer type in customer type list table."))
                .doOnError(error -> log.error("Failed to create" +
                        " customer type in customer type list table: {}", error.getLocalizedMessage()));
    }
}
