package com.panut.centralbanking.userservice.application.userprofile.query;

import com.panut.centralbanking.appcore.exceptions.NotFound;
import com.panut.centralbanking.userservice.infrastructure.model.UserProfile;
import com.panut.centralbanking.userservice.infrastructure.repository.UserProfileRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class UserProfileQuery {
    private final UserProfileRepository userProfileRepository;

    public UserProfileQuery(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    public Mono<UserProfile> findGeneric(final String userId) {
        return userProfileRepository.findById(userId)
                .switchIfEmpty(Mono.error(
                        () -> new NotFound("User id of " + userId + " does not " +
                                "have user profile.")
                ));
    }
}
