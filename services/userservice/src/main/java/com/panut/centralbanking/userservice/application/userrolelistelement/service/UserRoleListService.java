package com.panut.centralbanking.userservice.application.userrolelistelement.service;

import com.panut.centralbanking.userservice.core.UserRoleEnum;
import com.panut.centralbanking.userservice.infrastructure.model.UserRoleListElement;
import reactor.core.publisher.Mono;

public interface UserRoleListService {
    Mono<UserRoleListElement> add(UserRoleEnum userRoleEnum);
}
