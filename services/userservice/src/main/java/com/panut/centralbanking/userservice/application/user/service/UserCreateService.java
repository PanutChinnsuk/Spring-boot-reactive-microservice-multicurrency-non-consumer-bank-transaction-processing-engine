package com.panut.centralbanking.userservice.application.user.service;

import com.panut.centralbanking.userservice.api.response.UserCreatedData;
import reactor.core.publisher.Mono;

public interface UserCreateService {
    Mono<UserCreatedData> createUser(String customerId,
                                     String code);
}
