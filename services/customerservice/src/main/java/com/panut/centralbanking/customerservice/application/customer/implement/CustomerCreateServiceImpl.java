package com.panut.centralbanking.customerservice.application.customer.implement;

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
import com.panut.centralbanking.customerservice.application.customeruseractivationcode.service.CustomerUserActivationCodeCreateService;
import com.panut.centralbanking.customerservice.core.enums.CustomerStatus;
import com.panut.centralbanking.customerservice.infrastructure.model.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class CustomerCreateServiceImpl implements CustomerCreateService {
    private final CustomerProfileCreateService customerProfileCreateService;
    private final CustomerEmailCreateService customerEmailCreateService;
    private final CustomerPhoneCreateService customerPhoneCreateService;
    private final CustomerTypeCreateService customerTypeCreateService;
    private final CustomerRepositoryManager customerRepositoryManager;
    private final CustomerUserActivationCodeCreateService customerUserActivationCodeCreateService;

    public CustomerCreateServiceImpl(CustomerProfileCreateService customerProfileCreateService,
                                     CustomerEmailCreateService customerEmailCreateService,
                                     CustomerPhoneCreateService customerPhoneCreateService,
                                     CustomerTypeCreateService customerTypeCreateService,
                                     CustomerRepositoryManager customerRepositoryManager,
                                     CustomerUserActivationCodeCreateService customerUserActivationCodeCreateService) {
        this.customerProfileCreateService = customerProfileCreateService;
        this.customerEmailCreateService = customerEmailCreateService;
        this.customerPhoneCreateService = customerPhoneCreateService;
        this.customerTypeCreateService = customerTypeCreateService;
        this.customerRepositoryManager = customerRepositoryManager;
        this.customerUserActivationCodeCreateService = customerUserActivationCodeCreateService;
    }


    @Override
    public Mono<CustomerCreatedData> createCustomer(CustomerPhoneCreateData phoneData,
                                         CustomerEmailCreateData emailData,
                                         CustomerProfileCreateData profileData) {
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
                .flatMap(data -> customerUserActivationCodeCreateService
                        .createCode(data.getCustomerId())
                        .flatMap(activationCode -> data
                                .customerUserActivationCode(activationCode)))
                .doOnSuccess(a -> log.info("Created customer."))
                .doOnError(error -> log.error("Failed to create customer:" +
                        " {}", error.getLocalizedMessage()));


    }
}
