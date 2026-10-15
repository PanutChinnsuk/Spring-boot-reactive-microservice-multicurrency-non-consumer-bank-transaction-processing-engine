package com.panut.centralbanking.userservice.application.userrolelistelement.query;

import com.panut.centralbanking.appcore.exceptions.NotFound;
import com.panut.centralbanking.userservice.core.UserRoleEnum;
import com.panut.centralbanking.userservice.infrastructure.repository.UserRoleListElementRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class UserRoleListQuery {
    private final UserRoleListElementRepository userRoleListElementRepository;

    public UserRoleListQuery(UserRoleListElementRepository userRoleListElementRepository) {
        this.userRoleListElementRepository = userRoleListElementRepository;
    }



    public Mono<String> findIdGeneric(final UserRoleEnum userRoleEnum) {
        return userRoleListElementRepository.findIdByUserRoleEnum(userRoleEnum)
                .switchIfEmpty(Mono.error(() -> new NotFound("User role list does not" +
                        " contain " + userRoleEnum)));
    }
}
