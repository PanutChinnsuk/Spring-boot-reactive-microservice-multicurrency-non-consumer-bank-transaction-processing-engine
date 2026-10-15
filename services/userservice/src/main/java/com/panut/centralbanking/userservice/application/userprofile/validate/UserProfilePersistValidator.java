package com.panut.centralbanking.userservice.application.userprofile.validate;

import com.panut.centralbanking.appcore.exceptions.Conflicted;
import com.panut.centralbanking.userservice.infrastructure.model.UserProfile;
import com.panut.centralbanking.userservice.infrastructure.repository.UserProfileRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class UserProfilePersistValidator {
    private final UserProfileRepository userProfileRepository;

    public UserProfilePersistValidator(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }


    public Mono<String> validateDuplicatesUsername(final String username) {
        return userProfileRepository.existsByUsername(username)
                .flatMap(exist -> {
                    if (exist)
                        return Mono.error(() -> new Conflicted("Username already exists."));

                    return Mono.just(username);
                });
    }
}
