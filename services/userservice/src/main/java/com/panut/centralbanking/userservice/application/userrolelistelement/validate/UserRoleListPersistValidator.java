package com.panut.centralbanking.userservice.application.userrolelistelement.validate;

import com.panut.centralbanking.appcore.exceptions.Conflicted;
import com.panut.centralbanking.userservice.core.UserRoleEnum;
import com.panut.centralbanking.userservice.infrastructure.repository.UserRoleListElementRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class UserRoleListPersistValidator {
    private final UserRoleListElementRepository userRoleListElementRepository;

    public UserRoleListPersistValidator(UserRoleListElementRepository userRoleListElementRepository) {
        this.userRoleListElementRepository = userRoleListElementRepository;
    }


    public Mono<Void> validateDuplicates(UserRoleEnum userRoleEnum) {
        return userRoleListElementRepository.existsByUserRoleEnum(userRoleEnum)
                .flatMap(exist -> {
                    if (exist) return Mono.error(
                        () -> new Conflicted("User role list table already contain " + userRoleEnum));

                    return Mono.empty();
                });
    }
}
