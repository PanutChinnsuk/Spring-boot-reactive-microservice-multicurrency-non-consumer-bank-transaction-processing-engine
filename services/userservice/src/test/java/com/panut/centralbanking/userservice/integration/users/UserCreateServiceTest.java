package com.panut.centralbanking.userservice.integration.users;

import com.panut.centralbanking.appcore.common.response.CommonResult;
import com.panut.centralbanking.appsecuritycore.encoder.BCrypts;
import com.panut.centralbanking.userservice.IntegrationDev;
import com.panut.centralbanking.userservice.api.response.UserCreatedData;
import com.panut.centralbanking.userservice.api.service.customerservice.CustomerUserActivationCodeService;
import com.panut.centralbanking.userservice.api.service.customerservice.request.ActivateUserRequest;
import com.panut.centralbanking.userservice.api.service.customerservice.request.GetUserActivationCodeRequest;
import com.panut.centralbanking.userservice.api.service.customerservice.response.UserDefaultPasswordData;
import com.panut.centralbanking.userservice.application.user.service.UserCreateService;
import com.panut.centralbanking.userservice.infrastructure.model.UserCredentialProfile;
import com.panut.centralbanking.userservice.infrastructure.repository.UserCredentialProfileRepository;
import com.panut.centralbanking.userservice.infrastructure.repository.UserProfileRepository;
import com.panut.centralbanking.userservice.infrastructure.repository.UserRepository;
import com.panut.centralbanking.userservice.infrastructure.repository.UserRoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserCreateServiceTest implements IntegrationDev {
    @Autowired
    private UserCreateService userCreateService;

    @MockitoSpyBean
    private CustomerUserActivationCodeService customerUserActivationCodeService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserCredentialProfileRepository userCredentialProfileRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private BCrypts bCrypts;

    private String userId;
    private String userRoleId;

    @AfterAll
    public void cleanUp() {
        if (userRoleId != null) {
            userRoleRepository.deleteById(userRoleId).block();
        }

        if (userId != null) {
            userProfileRepository.deleteById(userId).block();
            userCredentialProfileRepository.deleteById(userId).block();
            userRepository.deleteById(userId).block();
        }

    }

    private UserDefaultPasswordData userDefaultPasswordData() {
        UserDefaultPasswordData userDefaultPasswordData = new UserDefaultPasswordData();
        userDefaultPasswordData.setFirstname("firstname");
        userDefaultPasswordData.setLastname("lastname");
        userDefaultPasswordData.setDefaultPassword("defaultPassword");

        return userDefaultPasswordData;
    }

    private GetUserActivationCodeRequest getUserActivationCodeRequest() {
        GetUserActivationCodeRequest getUserActivationCodeRequest = new GetUserActivationCodeRequest();
        getUserActivationCodeRequest.setCode("code");
        getUserActivationCodeRequest.setCustomerId("customerId");


        return getUserActivationCodeRequest;
    }

    private ActivateUserRequest activateUserRequest() {
        ActivateUserRequest activateUserRequest = new ActivateUserRequest();



        return activateUserRequest;
    }



    @Test
    public void shouldCreateUser() {
        Mono<UserDefaultPasswordData> data = Mono.just(userDefaultPasswordData());
        Mono<CommonResult> commonResult = Mono.just(CommonResult.defaultSuccess());

        String code = "code";
        String customerId = "cId";


        doReturn(data).when(customerUserActivationCodeService)
                .getCode(any(GetUserActivationCodeRequest.class));
        doReturn(commonResult).when(customerUserActivationCodeService)
                .consume(any(ActivateUserRequest.class));

        UserCreatedData result = userCreateService.createUser(customerId, code).block();

        this.userId = result.getUserId();
        this.userRoleId = result.getUserRoleId();

        Assertions.assertNotNull(result.getUserId());

        UserCredentialProfile cred = userCredentialProfileRepository.findById(this.userId).block();

        Assertions.assertTrue(bCrypts.matched(data.block().getDefaultPassword(),
                cred.getPasswordHash()).block());



    }
}
