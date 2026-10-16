package com.panut.centralbanking.userservice.application.user.implement;


import com.panut.centralbanking.userservice.api.response.UserCreatedData;
import com.panut.centralbanking.userservice.api.service.customerservice.CustomerUserActivationCodeService;
import com.panut.centralbanking.userservice.api.service.customerservice.request.ActivateUserRequest;
import com.panut.centralbanking.userservice.api.service.customerservice.request.GetUserActivationCodeRequest;
import com.panut.centralbanking.userservice.application.user.UserRepositoryManager;
import com.panut.centralbanking.userservice.application.user.service.UserCreateService;
import com.panut.centralbanking.userservice.application.usercredentialprofile.service.UserCredentialProfileCreateService;
import com.panut.centralbanking.userservice.application.userprofile.service.UserProfileCreateService;
import com.panut.centralbanking.userservice.application.userrole.query.UserRoleService;
import com.panut.centralbanking.userservice.infrastructure.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@Transactional(propagation = Propagation.REQUIRED)
public class UserCreateServiceImpl implements UserCreateService {
    private final UserRepositoryManager userRepositoryManager;
    private final CustomerUserActivationCodeService customerUserActivationCodeService;
    private final UserProfileCreateService userProfileCreateService;
    private final UserCredentialProfileCreateService userCredentialProfileCreateService;
    private final UserRoleService userRoleService;


    public UserCreateServiceImpl(UserRepositoryManager userRepositoryManager,
                                 CustomerUserActivationCodeService customerUserActivationCodeService,
                                 UserProfileCreateService userProfileCreateService,
                                 UserCredentialProfileCreateService userCredentialProfileCreateService,
                                 UserRoleService userRoleService) {
        this.userRepositoryManager = userRepositoryManager;
        this.customerUserActivationCodeService = customerUserActivationCodeService;
        this.userProfileCreateService = userProfileCreateService;
        this.userCredentialProfileCreateService = userCredentialProfileCreateService;
        this.userRoleService = userRoleService;
    }


    @Override
    public Mono<UserCreatedData> createUser(final String customerId,
                                            final String code) {
        return customerUserActivationCodeService
                .getCode(new GetUserActivationCodeRequest(customerId, code))
                .doOnNext(a -> log.info("Get valid user activation code."))
                .flatMap(defaultData ->
                        userRepositoryManager.save(User.builder().build())
                                .flatMap(user -> new UserCreatedData().user(user))
                                .flatMap(dataChain -> dataChain
                                        .defaultData(defaultData)))
                .doOnNext(a -> log.info("Created user mono chain."))
                .flatMap(data -> userProfileCreateService
                        .createUserProfile(data.getUserId(),
                                data.getDefaultData().getFirstname(),
                                data.getDefaultData().getLastname())
                        .flatMap(profile -> data.userProfile(profile))
                        .thenReturn(data))
                .flatMap(data -> userCredentialProfileCreateService
                        .createCredentialProfile(data.getUserId(), data.getDefaultData().getDefaultPassword())
                        .thenReturn(data))
                .flatMap(data -> userRoleService.addDefaultRole(data.getUserId())
                        .flatMap(userRole -> data.userRole(userRole))
                        .thenReturn(data))
                .flatMap(data -> customerUserActivationCodeService
                        .consume(new ActivateUserRequest(customerId, data.getUserId(), code))
                        .thenReturn(data))
                .doOnSuccess(a -> log.info("Successfully created user datas."))
                .doOnError(error -> log.error("Failed to create user datas, {}",
                        error.getLocalizedMessage()));
    }
}
