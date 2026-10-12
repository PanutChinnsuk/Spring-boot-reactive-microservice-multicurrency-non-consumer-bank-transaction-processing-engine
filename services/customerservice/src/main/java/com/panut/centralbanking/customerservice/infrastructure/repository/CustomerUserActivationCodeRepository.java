package com.panut.centralbanking.customerservice.infrastructure.repository;

import com.panut.centralbanking.customerservice.infrastructure.model.CustomerPhone;
import com.panut.centralbanking.customerservice.infrastructure.model.CustomerUserActivationCode;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CustomerUserActivationCodeRepository extends ReactiveCrudRepository
<CustomerUserActivationCode, String> {
}
