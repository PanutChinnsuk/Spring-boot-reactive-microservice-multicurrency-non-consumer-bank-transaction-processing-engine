package com.panut.centralbanking.userservice.application.userrole.query;

import com.panut.centralbanking.userservice.core.UserRoleEnum;
import com.panut.centralbanking.userservice.infrastructure.model.UserRole;
import reactor.core.publisher.Mono;

public interface UserRoleService {
    Mono<UserRole> addRole(String userId,
                           UserRoleEnum userRoleEnum);
    Mono<UserRole> addDefaultRole(String userId);
}
