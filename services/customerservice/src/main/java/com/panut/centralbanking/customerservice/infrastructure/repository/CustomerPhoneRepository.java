package com.panut.centralbanking.customerservice.infrastructure.repository;

import com.panut.centralbanking.customerservice.infrastructure.model.Customer;
import com.panut.centralbanking.customerservice.infrastructure.model.CustomerPhone;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CustomerPhoneRepository extends ReactiveCrudRepository
<CustomerPhone, String> {
}
