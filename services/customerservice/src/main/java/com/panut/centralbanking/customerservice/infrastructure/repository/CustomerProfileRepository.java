package com.panut.centralbanking.customerservice.infrastructure.repository;

import com.panut.centralbanking.customerservice.infrastructure.model.CustomerPhone;
import com.panut.centralbanking.customerservice.infrastructure.model.CustomerProfile;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CustomerProfileRepository extends ReactiveCrudRepository
<CustomerProfile, String> {
}
