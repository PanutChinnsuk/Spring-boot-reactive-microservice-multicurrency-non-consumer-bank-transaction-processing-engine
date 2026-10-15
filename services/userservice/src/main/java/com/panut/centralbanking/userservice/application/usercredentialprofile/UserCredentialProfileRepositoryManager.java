package com.panut.centralbanking.userservice.application.usercredentialprofile;

import com.panut.centralbanking.userservice.infrastructure.model.UserCredentialProfile;
import com.panut.centralbanking.userservice.infrastructure.repository.UserCredentialProfileRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class UserCredentialProfileRepositoryManager {
    private final UserCredentialProfileRepository userCredentialProfileRepository;

    public UserCredentialProfileRepositoryManager(UserCredentialProfileRepository userCredentialProfileRepository) {
        this.userCredentialProfileRepository = userCredentialProfileRepository;
    }

    public Mono<UserCredentialProfile> save(final UserCredentialProfile userCredentialProfile) {
        return this.userCredentialProfileRepository.save(userCredentialProfile);
    }
}
