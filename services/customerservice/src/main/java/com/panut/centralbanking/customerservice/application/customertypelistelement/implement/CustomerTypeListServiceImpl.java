package com.panut.centralbanking.customerservice.application.customertypelistelement.implement;

import com.panut.centralbanking.customerservice.application.customertypelistelement.CustomerTypeListElementRepositoryManager;
import com.panut.centralbanking.customerservice.application.customertypelistelement.service.CustomerTypeListService;
import com.panut.centralbanking.customerservice.application.customertypelistelement.validate.CustomerTypeListPersistValidator;
import com.panut.centralbanking.customerservice.core.enums.CustomerTypeEnum;
import com.panut.centralbanking.customerservice.infrastructure.model.CustomerTypeListElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class CustomerTypeListServiceImpl implements CustomerTypeListService {
    private final CustomerTypeListPersistValidator customerTypeListPersistValidator;
    private final CustomerTypeListElementRepositoryManager customerTypeListElementRepositoryManager;

    public CustomerTypeListServiceImpl(CustomerTypeListPersistValidator customerTypeListPersistValidator,
                                       CustomerTypeListElementRepositoryManager customerTypeListElementRepositoryManager) {
        this.customerTypeListPersistValidator = customerTypeListPersistValidator;
        this.customerTypeListElementRepositoryManager = customerTypeListElementRepositoryManager;
    }


    @Override
    public Mono<CustomerTypeListElement> add(final CustomerTypeEnum customerTypeEnum) {
        return customerTypeListPersistValidator
                .validateDuplicates(customerTypeEnum)
                .then(customerTypeListElementRepositoryManager.save(
                        CustomerTypeListElement.builder()
                                .customerTypeEnum(customerTypeEnum)
                                .build()))
                .doOnSuccess(a -> log.info("Created customer type in customer type list table."))
                .doOnError(error -> log.error("Failed to create" +
                        " customer type in customer type list table: {}", error.getLocalizedMessage()));
    }
}
