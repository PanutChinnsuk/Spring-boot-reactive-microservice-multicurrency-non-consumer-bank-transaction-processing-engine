package com.panut.centralbanking.customerservice.integration.customer;

import com.panut.centralbanking.appcore.common.response.CommonResult;
import com.panut.centralbanking.customerservice.api.data.CustomerEmailCreateData;
import com.panut.centralbanking.customerservice.api.data.CustomerPhoneCreateData;
import com.panut.centralbanking.customerservice.api.data.CustomerProfileCreateData;
import com.panut.centralbanking.customerservice.api.response.CustomerCreatedData;
import com.panut.centralbanking.customerservice.application.customer.service.CustomerCreateService;
import com.panut.centralbanking.customerservice.application.customeruseractivationcode.service.CustomerUserActivationCodeService;
import com.panut.centralbanking.customerservice.core.enums.CustomerStatus;
import com.panut.centralbanking.customerservice.core.enums.Gender;
import com.panut.centralbanking.customerservice.infrastructure.model.Customer;
import com.panut.centralbanking.customerservice.infrastructure.model.CustomerUserActivationCode;
import com.panut.centralbanking.customerservice.infrastructure.repository.*;
import com.panut.centralbanking.customerservice.integration.IntegrationDev;
import com.panut.centralbanking.customerservice.integration.IntegrationTestReference;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CustomerCycleServiceTest implements IntegrationDev {
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

    @Autowired
    private CustomerUserActivationCodeRepository customerUserActivationCodeRepository;

    @Autowired
    private CustomerUserActivationCodeService activationCodeService;

    private AtomicReference<String> parentId = new AtomicReference<>();
    private AtomicReference<String> defaultCustomerTypeId = new AtomicReference<>();
    private AtomicReference<String> userActivationCodeId = new AtomicReference<>();
    private AtomicReference<String> userActivationCode = new AtomicReference<>();
    private String citizenId = "11111111111111";


    private void cleanNonCustomerParent(String parentId) {
        customerPhoneRepository.deleteById(parentId).block();
        customerEmailRepository.deleteById(parentId).block();
        customerProfileRepository.deleteById(parentId).block();
    }

    @AfterAll
    public void cleanUp() {
        String parentId = this.parentId.get();
        String customerTypeId = this.defaultCustomerTypeId.get();
        String userActivationCodeId = this.userActivationCodeId.get();

        if (userActivationCodeId != null) {
            customerUserActivationCodeRepository.deleteById(userActivationCodeId)
                    .block();
        }

        if (customerTypeId != null) {
            customerTypeRepository.deleteById(customerTypeId).block();
        }

        if (parentId != null) {
            cleanNonCustomerParent(parentId);
            customerRepository.deleteById(parentId).block();
        }
    }

    @Test
    @Order(1)
    public void shouldCreateUser() {
        String preCreatedParentId = customerProfileRepository
                .findIdByCitizenId(citizenId).block();
        if (preCreatedParentId != null) cleanNonCustomerParent(preCreatedParentId);

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
        profileData.setCitizenId(citizenId);

        CommonResult<CustomerCreatedData> result = customerCreateService.createCustomer(
                phoneData,
                emailData,
                profileData).block();

        CustomerCreatedData created = result.getOptionalBody();
        Assertions.assertNotNull(created);

        String parentId = created.getCustomerId();
        Assertions.assertNotNull(parentId, "parentId == null");

        Assertions.assertEquals(parentId, created.getEmailParentId()
                , "parentId != created.getEmailParentId()");
        Assertions.assertEquals(parentId, created.getPhoneParentId(),
                "parentId != created.getPhoneParentId()");
        Assertions.assertEquals(parentId, created.getProfileParentId(),
                "parentId != created.getProfileParentId()");
        Assertions.assertEquals(parentId, created.getCustomerTypeParentId(),
                "parentId != created.getCustomerTypeParentId()");

        Assertions.assertNotNull(created.getUserId(), "created.getUserId() == null");
        Assertions.assertNotNull(created.getCustomerStatus(), "created.getCustomerStatus() == null");

        Assertions.assertNotNull(created.getEmail(), "created.getEmail() == null");
        Assertions.assertNotNull(created.getVerifiedEmail(), "created.getVerifiedEmail() == null");

        Assertions.assertNotNull(created.getPhone(), "created.getPhone() == null");
        Assertions.assertNotNull(created.getVerifiedPhone(), "created.getVerifiedPhone() == null");

        Assertions.assertNotNull(created.getFirstname(), "created.getFirstname() == null");
        Assertions.assertNotNull(created.getLastname(), "created.getLastname() == null");
        Assertions.assertNotNull(created.getAge(), "created.getAge() == null");
        Assertions.assertNotNull(created.getAddress(), "created.getAddress() == null");
        Assertions.assertNotNull(created.getGender(), "created.getGender() == null");
        Assertions.assertNotNull(created.getCitizenid(), "created.getCitizenid() == null");


        Assertions.assertNotNull(created.getCustomerTypeId(), "created.getCustomerTypeId() == null");

        Assertions.assertNotNull(created.getCustomerUserActivationCodeId(), "created.getCustomerUserActivationCodeId() == null");
        Assertions.assertNotNull(created.getCustomerUserActivationCode(), "created.getCustomerUserActivationCode() == null");


        this.parentId.set(parentId);
        this.defaultCustomerTypeId.set(created.getCustomerTypeId());
        this.userActivationCodeId.set(created.getCustomerUserActivationCodeId());
        this.userActivationCode.set(created.getCustomerUserActivationCode());

        Customer customer = customerRepository
                .findById(parentId).block();
        Assertions.assertEquals(0L,customer.getVersion(), "Customer.getVersion() != 0");


        Assertions.assertEquals(1L, customerRepository
                .save(customer.changeCustomerStatus(CustomerStatus.ACTIVE_WITH_USER)
                        .block()).block().getVersion());


    }

    @Test
    @Order(2)
    public void shouldConsumedAndAppliedUser() {
        String customerId = this.parentId.get();
        String code = this.userActivationCode.get();
        String codeId = this.userActivationCodeId.get();
        String userId = IntegrationTestReference.TEST_USER_ID;

        Assertions.assertNotNull(customerId, "customerId != null");
        Assertions.assertNotNull(code, "code != null");
        Assertions.assertNotNull(codeId, "codeId != null");

        CommonResult result = activationCodeService
                .consumeValidCode(customerId, code, userId).block();

        Assertions.assertTrue(result.isSuccess(), "result.isSuccess != true");
        Assertions.assertEquals(citizenId, result.getOptionalBody());

        Assertions.assertTrue(
                customerUserActivationCodeRepository
                        .findById(codeId)
                        .block()
                        .getRevoked(), "Assert customerUserActivationCode.revoked" +
                        " != true."
        );

        Customer customer = customerRepository.findById(customerId).block();
        Assertions.assertEquals(CustomerStatus.ACTIVE_WITH_USER,
                customer.getCustomerStatus(), "customer.getCustomerStatus " +
                        "!= CustomerStatus.ACTIVE_WITH_USER");
        Assertions.assertEquals(userId, customer.getUserId(),
                "userId != customer.getUserId.");


    }

}
