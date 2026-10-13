package com.panut.centralbanking.customerservice.application.customer.service;

import com.panut.centralbanking.customerservice.api.data.CustomerEmailCreateData;
import com.panut.centralbanking.customerservice.api.data.CustomerPhoneCreateData;
import com.panut.centralbanking.customerservice.api.data.CustomerProfileCreateData;
import com.panut.centralbanking.customerservice.api.response.CustomerCreatedData;
import reactor.core.publisher.Mono;

public interface CustomerCreateService {
    Mono<CustomerCreatedData> createCustomer(CustomerPhoneCreateData phoneData,
                                             CustomerEmailCreateData emailData,
                                             CustomerProfileCreateData profileData);
}
