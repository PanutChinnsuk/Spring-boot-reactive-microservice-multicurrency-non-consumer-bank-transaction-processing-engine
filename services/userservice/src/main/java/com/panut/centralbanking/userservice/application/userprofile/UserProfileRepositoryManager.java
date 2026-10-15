package com.panut.centralbanking.userservice.application.userprofile;

import com.panut.centralbanking.userservice.infrastructure.model.UserProfile;
import com.panut.centralbanking.userservice.infrastructure.repository.UserProfileRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class UserProfileRepositoryManager {
    private final UserProfileRepository userProfileRepository;

    public UserProfileRepositoryManager(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }


    public Mono<UserProfile> save(final UserProfile userProfile) {
        return this.userProfileRepository.save(userProfile);
    }
}
