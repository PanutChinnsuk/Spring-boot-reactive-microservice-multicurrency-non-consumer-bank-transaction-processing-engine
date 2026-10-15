package com.panut.centralbanking.userservice.application.user.implement;

import com.panut.centralbanking.appcore.servicemaps.map.CustomerService;
import com.panut.centralbanking.userservice.application.user.UserRepositoryManager;
import com.panut.centralbanking.userservice.application.usercredentialprofile.service.UserCredentialProfileCreateService;
import com.panut.centralbanking.userservice.application.userprofile.service.UserProfileCreateService;
import com.panut.centralbanking.userservice.application.userrole.query.UserRoleService;
import org.springframework.stereotype.Service;

@Service
public class UserCreateServiceImpl {
    private final UserRepositoryManager userRepositoryManager;
    private final CustomerService customerService;
    private final UserProfileCreateService userProfileCreateService;
    private final UserCredentialProfileCreateService userCredentialProfileCreateService;
    private final UserRoleService userRoleService;

    public UserCreateServiceImpl(UserRepositoryManager userRepositoryManager,
                                 CustomerService customerService,
                                 UserProfileCreateService userProfileCreateService,
                                 UserCredentialProfileCreateService userCredentialProfileCreateService,
                                 UserRoleService userRoleService) {
        this.userRepositoryManager = userRepositoryManager;
        this.customerService = customerService;
        this.userProfileCreateService = userProfileCreateService;
        this.userCredentialProfileCreateService = userCredentialProfileCreateService;
        this.userRoleService = userRoleService;
    }
}
