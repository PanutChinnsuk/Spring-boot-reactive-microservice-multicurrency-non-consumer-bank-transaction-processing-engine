package com.panut.centralbanking.customerservice.integration.customer;

import com.panut.centralbanking.appcore.common.response.CommonResult;
import com.panut.centralbanking.appcore.exceptions.Conflicted;
import com.panut.centralbanking.customerservice.api.data.CustomerEmailCreateData;
import com.panut.centralbanking.customerservice.api.data.CustomerPhoneCreateData;
import com.panut.centralbanking.customerservice.api.data.CustomerProfileCreateData;
import com.panut.centralbanking.customerservice.api.response.CustomerCreatedData;
import com.panut.centralbanking.customerservice.api.response.UserDefaultPasswordData;
import com.panut.centralbanking.customerservice.application.customer.service.CustomerCreateService;
import com.panut.centralbanking.customerservice.application.customertype.service.CustomerTypeCreateService;
import com.panut.centralbanking.customerservice.application.customeruseractivationcode.service.CustomerUserActivationCodeService;
import com.panut.centralbanking.customerservice.core.enums.CustomerStatus;
import com.panut.centralbanking.customerservice.core.enums.CustomerTypeEnum;
import com.panut.centralbanking.customerservice.core.enums.Gender;
import com.panut.centralbanking.customerservice.infrastructure.model.Customer;
import com.panut.centralbanking.customerservice.infrastructure.repository.*;
import com.panut.centralbanking.customerservice.IntegrationDev;
import com.panut.centralbanking.customerservice.integration.IntegrationTestReference;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CustomerCreateServiceTest implements IntegrationDev {
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

    @Autowired
    private CustomerTypeCreateService customerTypeCreateService;

    private String parentId;
    private String defaultCustomerTypeId;
    private CustomerProfileCreateData customerProfileCreateData;
    private String userActivationCodeId;
    private String userActivationCode;



    private void cleanNonCustomerParent(String parentId) {
        customerPhoneRepository.deleteById(parentId).block();
        customerEmailRepository.deleteById(parentId).block();
        customerProfileRepository.deleteById(parentId).block();
    }

    @AfterAll
    public void cleanUp() {
        if (userActivationCodeId != null) {
            customerUserActivationCodeRepository.deleteById(userActivationCodeId)
                    .block();
        }

        if (defaultCustomerTypeId != null) {
            customerTypeRepository.deleteById(defaultCustomerTypeId).block();
        }

        if (parentId != null) {
            cleanNonCustomerParent(parentId);
            customerRepository.deleteById(parentId).block();
        }
    }


    private CustomerEmailCreateData customerEmailCreateData() {
        CustomerEmailCreateData emailData = new CustomerEmailCreateData();
        emailData.setEmail(UUID.randomUUID().toString() + "@gmail.com");

        return emailData;
    }

    private CustomerPhoneCreateData customerPhoneCreateData() {
        CustomerPhoneCreateData phoneData = new CustomerPhoneCreateData();
        phoneData.setPhone(UUID.randomUUID().toString());

        return phoneData;
    }

    private CustomerProfileCreateData customerProfileCreateData() {
        CustomerProfileCreateData profileData = new CustomerProfileCreateData();
        profileData.setAddress("address");
        profileData.setAge(18);
        profileData.setFirstname("firstname");
        profileData.setLastname("lastname");
        profileData.setGender(Gender.MALE);
        profileData.setCitizenId("citIdzen");

        return profileData;
    }

    @Test
    @Order(1)
    public void shouldCreateCustomer() {
        CustomerEmailCreateData emailData = customerEmailCreateData();
        CustomerPhoneCreateData phoneData = customerPhoneCreateData();
        CustomerProfileCreateData profileData = customerProfileCreateData();


        CommonResult<CustomerCreatedData> result = customerCreateService.createCustomer(
                phoneData,
                emailData,
                profileData).block();

        CustomerCreatedData created = result.getOptionalBody();
        Assertions.assertNotNull(created);

        String parentId = created.getCustomerId();
        Assertions.assertNotNull(parentId, "parentId == null");


        this.parentId = parentId;
        this.defaultCustomerTypeId = created.getCustomerTypeId();
        this.userActivationCodeId = created.getCustomerUserActivationCodeId();
        this.userActivationCode = created.getCustomerUserActivationCode();
        this.customerProfileCreateData = profileData;

        Customer customer = customerRepository
                .findById(parentId).block();
        Assertions.assertEquals(0L,customer.getVersion(), "Customer.getVersion() != 0");


        Assertions.assertEquals(1L, customerRepository
                .save(customer.changeCustomerStatus(CustomerStatus.ACTIVE_WITH_USER)
                        .block()).block().getVersion());


        Assertions.assertThrows(Conflicted.class,
                () -> customerTypeCreateService.addCustomerType(
                        parentId, CustomerTypeEnum.REGULAR)
                        .block(), "Throw conflict, adding duplicated customer type.");


    }

    @Test
    @Order(2)
    public void shouldConsumedAndAppliedUser() {
        String customerId = this.parentId;
        String code = this.userActivationCode;
        String codeId = this.userActivationCodeId;
        String userId = "TEST";
        CustomerProfileCreateData fromProfile = this.customerProfileCreateData;

        Assertions.assertNotNull(customerId, "customerId == null");
        Assertions.assertNotNull(code, "code == null");
        Assertions.assertNotNull(codeId, "codeId == null");
        Assertions.assertNotNull(fromProfile, "fromProfile == null");

        CommonResult<UserDefaultPasswordData> data = activationCodeService
                .getCode(customerId, code).block();
        UserDefaultPasswordData body = data.getOptionalBody();

        Assertions.assertTrue(data.isSuccess(), "result.isSuccess != true");
        Assertions.assertNotNull(body.getDefaultPassword(), "body.getDefaultPassword() == null");
        Assertions.assertEquals(fromProfile.getFirstname(),
                body.getFirstname(),
                "fromProfile.getFirstname != body.getFirstname");
        Assertions.assertEquals(fromProfile.getLastname(),
                body.getLastname(),
                "fromProfile.getLastname != body.getLastname");

        CommonResult consumed = activationCodeService.consume(
                customerId, code, userId).block();



        Customer customer = customerRepository.findById(customerId).block();
        Assertions.assertEquals(CustomerStatus.ACTIVE_WITH_USER,
                customer.getCustomerStatus(), "customer.getCustomerStatus " +
                        "!= CustomerStatus.ACTIVE_WITH_USER");
        Assertions.assertEquals(userId, customer.getUserId(),
                "userId != customer.getUserId.");

        Assertions.assertTrue(
                customerUserActivationCodeRepository
                        .findById(codeId)
                        .block()
                        .getRevoked(), "Assert customerUserActivationCode.revoked" +
                        " != true."
        );


    }

}
