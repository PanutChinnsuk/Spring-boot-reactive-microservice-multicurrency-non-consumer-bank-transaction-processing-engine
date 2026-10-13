package com.panut.centralbanking.customerservice.integration.customer;

import com.panut.centralbanking.customerservice.api.data.CustomerEmailCreateData;
import com.panut.centralbanking.customerservice.api.data.CustomerPhoneCreateData;
import com.panut.centralbanking.customerservice.api.data.CustomerProfileCreateData;
import com.panut.centralbanking.customerservice.api.response.CustomerCreatedData;
import com.panut.centralbanking.customerservice.application.customer.service.CustomerCreateService;
import com.panut.centralbanking.customerservice.core.enums.Gender;
import com.panut.centralbanking.customerservice.infrastructure.repository.*;
import com.panut.centralbanking.customerservice.integration.IntegrationDev;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.atomic.AtomicReference;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CustomerCreateTest implements IntegrationDev {
    @Autowired
    private CustomerCreateService customerCreateService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerTypeRepository customerTypeRepository;

    @Autowired
    private CustomerProfileRepository customerProfileRepository;

    @Autowired
    private CustomerEmailRepository customerEmailRepository;

    @Autowired
    private CustomerPhoneRepository customerPhoneRepository;

    private AtomicReference<String> parentId = new AtomicReference<>();
    private AtomicReference<String> defaultCustomerTypeId = new AtomicReference<>();




    @AfterAll
    public void cleanUp() {
        String parentId = this.parentId.get();
        String customerTypeId = this.defaultCustomerTypeId.get();

        if (parentId != null) {
            customerPhoneRepository.deleteById(parentId).block();
            customerEmailRepository.deleteById(parentId).block();
            customerProfileRepository.deleteById(parentId).block();
            customerRepository.deleteById(parentId).block();
        }

        if (customerTypeId != null) {
            customerTypeRepository.deleteById(customerTypeId).block();
        }


    }

    @Test
    public void shouldCreateUser() {
        CustomerEmailCreateData emailData = new CustomerEmailCreateData();
        CustomerPhoneCreateData phoneData = new CustomerPhoneCreateData();
        CustomerProfileCreateData profileData = new CustomerProfileCreateData();

        emailData.setEmail("customerEmail1@gmail.com");
        phoneData.setPhone("0888888888");

        profileData.setAddress("address");
        profileData.setAge(18);
        profileData.setFirstname("firstname");
        profileData.setLastname("lastname");
        profileData.setGender(Gender.MALE);

        CustomerCreatedData created = customerCreateService.createCustomer(
                phoneData,
                emailData,
                profileData).block();

        String parentId = created.getCustomerId();
        Assertions.assertNotNull(parentId);
        Assertions.assertEquals(parentId, created.getEmailParentId());
        Assertions.assertEquals(parentId, created.getPhoneParentId());
        Assertions.assertEquals(parentId, created.getProfileParentId());
        Assertions.assertEquals(parentId, created.getCustomerTypeParentId());

        Assertions.assertNotNull(created.getUserId());
        Assertions.assertNotNull(created.getCustomerStatus());

        Assertions.assertNotNull(created.getEmail());
        Assertions.assertNotNull(created.getVerifiedEmail());

        Assertions.assertNotNull(created.getPhone());
        Assertions.assertNotNull(created.getVerifiedPhone());

        Assertions.assertNotNull(created.getFirstname());
        Assertions.assertNotNull(created.getLastname());
        Assertions.assertNotNull(created.getAge());
        Assertions.assertNotNull(created.getAddress());
        Assertions.assertNotNull(created.getGender());

        Assertions.assertNotNull(created.getCustomerTypeId());


        this.parentId.set(parentId);
        this.defaultCustomerTypeId.set(created.getCustomerTypeId());

    }

}
