package com.panut.centralbanking.customerservice.application.customertype.implement;

import com.panut.centralbanking.customerservice.application.customer.query.CustomerQuery;
import com.panut.centralbanking.customerservice.application.customertype.CustomerTypeRepositoryManager;
import com.panut.centralbanking.customerservice.application.customertype.service.CustomerTypeCreateService;
import com.panut.centralbanking.customerservice.application.customertype.validate.CustomerTypePersistValidator;
import com.panut.centralbanking.customerservice.application.customertypelistelement.query.CustomerTypeListQuery;
import com.panut.centralbanking.customerservice.core.enums.CustomerTypeEnum;
import com.panut.centralbanking.customerservice.infrastructure.model.CustomerType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class CustomerTypeCreateServiceImpl implements CustomerTypeCreateService {
    private final CustomerTypeRepositoryManager customerTypeRepositoryManager;
    private final CustomerTypePersistValidator customerTypePersistValidator;
    private final CustomerTypeListQuery customerTypeListQuery;
    private final CustomerQuery customerQuery;

    public CustomerTypeCreateServiceImpl(CustomerTypeRepositoryManager customerTypeRepositoryManager,
                                         CustomerTypePersistValidator customerTypePersistValidator,
                                         CustomerTypeListQuery customerTypeListQuery,
                                         CustomerQuery customerQuery) {
        this.customerTypeRepositoryManager = customerTypeRepositoryManager;
        this.customerTypePersistValidator = customerTypePersistValidator;
        this.customerTypeListQuery = customerTypeListQuery;
        this.customerQuery = customerQuery;
    }

    private Mono<CustomerType> insert(final CustomerTypeEnum customerTypeEnum,
                                      final String customerId) {
        return  customerTypeListQuery.findIdGeneric(customerTypeEnum)
                .flatMap(elementId -> customerTypeRepositoryManager
                        .save(CustomerType.builder()
                                .customerId(customerId)
                                .customerTypeListId(elementId)
                                .build()));
    }

    @Override
    public Mono<CustomerType> addCustomerType(final String customerId,
                                              final CustomerTypeEnum customerTypeEnum) {
        return customerQuery.findExistsThrow(customerId)
                .then(customerTypePersistValidator.validateDuplicates(customerTypeEnum,
                        customerId))
                .then(insert(customerTypeEnum, customerId))
                .doOnSuccess(a -> log.info("Created customer type for "
                + customerId))
                .doOnError(error -> log.error("Failed to create customer type:" +
                        " {}", error.getLocalizedMessage()));
    }

    @Override
    public Mono<CustomerType> addDefault(final String customerId) {
        return insert(CustomerTypeEnum.REGULAR, customerId)
                .doOnSuccess(a -> log.info("Created default customer type."))
                .doOnError(error -> log.error("Failed to create default customer type:" +
                        " {}", error.getLocalizedMessage()));
    }
}
