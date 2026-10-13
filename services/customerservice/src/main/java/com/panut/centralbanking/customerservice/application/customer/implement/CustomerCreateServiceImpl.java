package com.panut.centralbanking.customerservice.application.customer.implement;

import com.panut.centralbanking.customerservice.api.data.CustomerEmailCreateData;
import com.panut.centralbanking.customerservice.api.data.CustomerPhoneCreateData;
import com.panut.centralbanking.customerservice.api.data.CustomerProfileCreateData;
import com.panut.centralbanking.customerservice.application.customer.CustomerRepositoryManager;
import com.panut.centralbanking.customerservice.application.customeremail.service.CustomerEmailCreateService;
import com.panut.centralbanking.customerservice.application.customerphone.service.CustomerPhoneCreateService;
import com.panut.centralbanking.customerservice.application.customerprofile.service.CustomerProfileCreateService;
import com.panut.centralbanking.customerservice.application.customertype.service.CustomerTypeCreateService;
import com.panut.centralbanking.customerservice.core.enums.CustomerStatus;
import com.panut.centralbanking.customerservice.infrastructure.model.Customer;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CustomerCreateServiceImpl {
    private final CustomerProfileCreateService customerProfileCreateService;
    private final CustomerEmailCreateService customerEmailCreateService;
    private final CustomerPhoneCreateService customerPhoneCreateService;
    private final CustomerTypeCreateService customerTypeCreateService;
    private final CustomerRepositoryManager customerRepositoryManager;

    public CustomerCreateServiceImpl(CustomerProfileCreateService customerProfileCreateService,
                                     CustomerEmailCreateService customerEmailCreateService,
                                     CustomerPhoneCreateService customerPhoneCreateService,
                                     CustomerTypeCreateService customerTypeCreateService,
                                     CustomerRepositoryManager customerRepositoryManager) {
        this.customerProfileCreateService = customerProfileCreateService;
        this.customerEmailCreateService = customerEmailCreateService;
        this.customerPhoneCreateService = customerPhoneCreateService;
        this.customerTypeCreateService = customerTypeCreateService;
        this.customerRepositoryManager = customerRepositoryManager;
    }



    public Mono<Customer> createCustomer(CustomerPhoneCreateData phoneData,
                                         CustomerEmailCreateData emailData,
                                         CustomerProfileCreateData profileData) {
        return customerRepositoryManager.save(
                Customer.builder()
                        .customerStatus(CustomerStatus.ACTIVE_NO_USER)
                        .build());
    }
}
