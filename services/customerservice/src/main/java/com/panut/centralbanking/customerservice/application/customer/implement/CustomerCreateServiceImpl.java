package com.panut.centralbanking.customerservice.application.customer.implement;

import com.panut.centralbanking.appcore.common.response.CommonResult;
import com.panut.centralbanking.customerservice.api.data.CustomerEmailCreateData;
import com.panut.centralbanking.customerservice.api.data.CustomerPhoneCreateData;
import com.panut.centralbanking.customerservice.api.data.CustomerProfileCreateData;
import com.panut.centralbanking.customerservice.api.response.CustomerCreatedData;
import com.panut.centralbanking.customerservice.application.customer.CustomerRepositoryManager;
import com.panut.centralbanking.customerservice.application.customer.service.CustomerCreateService;
import com.panut.centralbanking.customerservice.application.customeremail.service.CustomerEmailCreateService;
import com.panut.centralbanking.customerservice.application.customerphone.service.CustomerPhoneCreateService;
import com.panut.centralbanking.customerservice.application.customerprofile.service.CustomerProfileCreateService;
import com.panut.centralbanking.customerservice.application.customertype.service.CustomerTypeCreateService;
import com.panut.centralbanking.customerservice.application.customeruseractivationcode.service.CustomerUserActivationCodeService;
import com.panut.centralbanking.customerservice.core.enums.CustomerStatus;
import com.panut.centralbanking.customerservice.infrastructure.model.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@Transactional(propagation = Propagation.REQUIRED)
public class CustomerCreateServiceImpl implements CustomerCreateService {
    private final CustomerProfileCreateService customerProfileCreateService;
    private final CustomerEmailCreateService customerEmailCreateService;
    private final CustomerPhoneCreateService customerPhoneCreateService;
    private final CustomerTypeCreateService customerTypeCreateService;
    private final CustomerRepositoryManager customerRepositoryManager;
    private final CustomerUserActivationCodeService customerUserActivationCodeService;

    public CustomerCreateServiceImpl(CustomerProfileCreateService customerProfileCreateService,
                                     CustomerEmailCreateService customerEmailCreateService,
                                     CustomerPhoneCreateService customerPhoneCreateService,
                                     CustomerTypeCreateService customerTypeCreateService,
                                     CustomerRepositoryManager customerRepositoryManager,
                                     CustomerUserActivationCodeService customerUserActivationCodeService) {
        this.customerProfileCreateService = customerProfileCreateService;
        this.customerEmailCreateService = customerEmailCreateService;
        this.customerPhoneCreateService = customerPhoneCreateService;
        this.customerTypeCreateService = customerTypeCreateService;
        this.customerRepositoryManager = customerRepositoryManager;
        this.customerUserActivationCodeService = customerUserActivationCodeService;
    }


    @Override
    public Mono<CommonResult<CustomerCreatedData>> createCustomer(
            final CustomerPhoneCreateData phoneData,
            final CustomerEmailCreateData emailData,
            final CustomerProfileCreateData profileData) {
        return customerRepositoryManager.save(
                Customer.builder()
                        .customerStatus(CustomerStatus.ACTIVE_NO_USER)
                        .build())
                .doOnNext(a -> log.info("Created customer mono chain."))
                .flatMap(customer -> new CustomerCreatedData().customer(customer))
                .flatMap(data -> customerEmailCreateService
                        .createDefaultCustomerEmail(emailData, data.getCustomerId())
                        .flatMap(email -> data.customerEmail(email)))
                .flatMap(data -> customerPhoneCreateService
                        .createDefaultCustomerPhone(phoneData, data.getCustomerId())
                        .flatMap(phone -> data.customerPhone(phone)))
                .flatMap(data -> customerProfileCreateService
                        .createCustomerProfile(profileData, data.getCustomerId())
                        .flatMap(profile -> data.customerProfile(profile)))
                .flatMap(data -> customerTypeCreateService
                        .addDefault(data.getCustomerId())
                        .flatMap(customerType -> data
                                .customerType(customerType)))
                .flatMap(data -> customerUserActivationCodeService
                        .createCode(data.getCustomerId())
                        .flatMap(activationCode -> data
                                .customerUserActivationCode(activationCode)))
                .map(data -> new CommonResult<CustomerCreatedData>(
                        "Successfully created customer.", data))
                .doOnSuccess(a -> log.info("Created customer."))
                .doOnError(error -> log.error("Failed to create customer: {}"
                        , error.getLocalizedMessage()));


    }
}
