package com.panut.centralbanking.userservice.application.userrole.validate;

import com.panut.centralbanking.appcore.exceptions.Conflicted;
import com.panut.centralbanking.userservice.core.UserRoleEnum;
import com.panut.centralbanking.userservice.infrastructure.repository.UserRoleRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class UserRolePersistValidator {
    private final UserRoleRepository userRoleRepository;

    public UserRolePersistValidator(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }


    public Mono<Void> validateDuplicates(final String userId,
                                         final UserRoleEnum adding) {
        return userRoleRepository.alreadyExists(userId, adding)
                .flatMap(exist -> {
                    return Mono.error(
                            () -> new Conflicted("User already have this user role."));
                })
                .then();
    }
}
