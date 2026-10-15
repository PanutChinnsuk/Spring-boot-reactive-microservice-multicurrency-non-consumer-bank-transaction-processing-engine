package com.panut.centralbanking.userservice.application.usercredentialprofile.service;

import com.panut.centralbanking.userservice.application.usercredentialprofile.UserCredentialProfileRepositoryManager;
import com.panut.centralbanking.userservice.infrastructure.model.UserCredentialProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class UserCredentialProfileCreateService {
    private final UserCredentialProfileRepositoryManager repoManager;

    public UserCredentialProfileCreateService(UserCredentialProfileRepositoryManager repoManager) {
        this.repoManager = repoManager;
    }


    public Mono<UserCredentialProfile> createCredentialProfile(final String userId,
                                                               final String customerDefaultPassword) {
        return repoManager.save(UserCredentialProfile
                .builder()
                .userId(userId)
                .passwordHash(customerDefaultPassword)
                .build())
                .doOnSuccess(a -> log.info("Created user credential profile."))
                .doOnError(error -> log.error("Failed to create user credential profile, {}",
                        error.getLocalizedMessage()));
    }
}
