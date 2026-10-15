package com.panut.centralbanking.userservice.infrastructure.repository;


import com.panut.centralbanking.userservice.core.UserRoleEnum;
import com.panut.centralbanking.userservice.infrastructure.model.UserRoleListElement;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRoleListElementRepository
        extends ReactiveCrudRepository<UserRoleListElement, String> {

    Mono<Boolean> existsByUserRoleEnum(UserRoleEnum userRoleEnum);
}
