package com.panut.centralbanking.customerservice.infrastructure.repository;


import com.panut.centralbanking.customerservice.infrastructure.model.CustomerContactVerificationCode;
import com.panut.centralbanking.customerservice.infrastructure.model.CustomerEmail;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CustomerContactVerificationCodeRepository extends ReactiveCrudRepository
<CustomerContactVerificationCode, String> {
}
