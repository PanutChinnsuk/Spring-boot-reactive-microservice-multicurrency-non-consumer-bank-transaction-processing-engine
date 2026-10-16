package com.panut.centralbanking.userservice.application.userprofile.service;

import com.panut.centralbanking.userservice.application.userprofile.UserProfileRepositoryManager;
import com.panut.centralbanking.userservice.application.userprofile.validate.UserProfilePersistValidator;
import com.panut.centralbanking.userservice.infrastructure.model.UserProfile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;

@Service
@Slf4j
public class UserProfileCreateService {
    private final UserProfileRepositoryManager userProfileRepositoryManager;
    private final UserProfilePersistValidator userProfilePersistValidator;

    public UserProfileCreateService(UserProfileRepositoryManager userProfileRepositoryManager,
                                    UserProfilePersistValidator userProfilePersistValidator) {
        this.userProfileRepositoryManager = userProfileRepositoryManager;
        this.userProfilePersistValidator = userProfilePersistValidator;
    }

    public Mono<String> usernameFormat(final String customerFirstname,
                                       final String customerLastname) {
        return Mono.defer(() -> Mono.just(
                String.join("_", customerFirstname.replace(" ", "_"),
                        customerLastname.replace(" ", "_"))
        ));
    }


    public Mono<UserProfile> createUserProfile(final String userId,
                                               final String customerFirstname,
                                               final String customerLastname) {

        return usernameFormat(customerFirstname, customerLastname)
                .flatMap(formatted -> userProfilePersistValidator
                        .validateDuplicatesUsername(formatted))
                .flatMap(formatted -> userProfileRepositoryManager.save(
                        UserProfile.builder()
                                .userId(userId)
                                .username(formatted)
                                .build()))
                .doOnNext(a -> log.info("After saving customer profile."))
                .doOnSuccess(a -> log.info("Created user profile."))
                .doOnError(error -> log.error("Failed to create user profile, {}",
                        error.getLocalizedMessage()));
    }
}
