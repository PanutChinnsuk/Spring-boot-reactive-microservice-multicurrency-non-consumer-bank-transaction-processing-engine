package com.panut.centralbanking.customerservice.infrastructure.repository;

import com.panut.centralbanking.customerservice.infrastructure.model.CustomerPhone;
import com.panut.centralbanking.customerservice.infrastructure.model.CustomerType;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CustomerTypeRepository extends ReactiveCrudRepository
<CustomerType, String> {
}
