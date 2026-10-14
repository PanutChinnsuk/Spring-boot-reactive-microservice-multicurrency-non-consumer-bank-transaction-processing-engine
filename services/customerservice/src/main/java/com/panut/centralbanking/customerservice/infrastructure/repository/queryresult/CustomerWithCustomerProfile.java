package com.panut.centralbanking.customerservice.infrastructure.repository.queryresult;

import com.panut.centralbanking.customerservice.infrastructure.model.Customer;
import lombok.Data;

@Data
public class CustomerWithCustomerProfile {
    private Customer customer;
    private String citizenId;
}
