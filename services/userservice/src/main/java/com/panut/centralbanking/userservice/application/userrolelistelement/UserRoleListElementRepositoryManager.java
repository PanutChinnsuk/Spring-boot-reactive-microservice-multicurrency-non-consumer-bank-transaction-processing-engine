package com.panut.centralbanking.userservice.application.userrolelistelement;

import com.panut.centralbanking.userservice.infrastructure.model.UserRoleListElement;
import com.panut.centralbanking.userservice.infrastructure.repository.UserRoleListElementRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class UserRoleListElementRepositoryManager {
    private final UserRoleListElementRepository userRoleListElementRepository;

    public UserRoleListElementRepositoryManager(UserRoleListElementRepository userRoleListElementRepository) {
        this.userRoleListElementRepository = userRoleListElementRepository;
    }

    public Mono<UserRoleListElement> save(final UserRoleListElement userRoleListElement) {
        return this.userRoleListElementRepository.save(userRoleListElement);
    }
}
