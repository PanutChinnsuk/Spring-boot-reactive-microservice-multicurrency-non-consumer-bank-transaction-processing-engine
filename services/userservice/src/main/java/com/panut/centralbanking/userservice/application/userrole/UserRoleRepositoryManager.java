package com.panut.centralbanking.userservice.application.userrole;

import com.panut.centralbanking.userservice.infrastructure.model.UserRole;
import com.panut.centralbanking.userservice.infrastructure.repository.UserRoleRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class UserRoleRepositoryManager {
    private final UserRoleRepository userRoleRepository;

    public UserRoleRepositoryManager(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    public Mono<UserRole> save(final UserRole userRole) {
        return this.userRoleRepository.save(userRole);
    }
}
