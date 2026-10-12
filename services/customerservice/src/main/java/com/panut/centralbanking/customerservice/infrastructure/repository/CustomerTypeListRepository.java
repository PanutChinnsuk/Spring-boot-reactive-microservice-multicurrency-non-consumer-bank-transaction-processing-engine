package com.panut.centralbanking.customerservice.infrastructure.repository;

import com.panut.centralbanking.customerservice.infrastructure.model.CustomerPhone;
import com.panut.centralbanking.customerservice.infrastructure.model.CustomerTypeList;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CustomerTypeListRepository extends ReactiveCrudRepository
<CustomerTypeList, String> {
}
