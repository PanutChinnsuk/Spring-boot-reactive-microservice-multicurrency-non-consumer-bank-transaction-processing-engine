package com.panut.centralbanking.userservice.application.user.query;

import com.panut.centralbanking.appcore.exceptions.NotFound;
import com.panut.centralbanking.userservice.infrastructure.repository.UserRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class UserQuery {
    private final UserRepository userRepository;

    public UserQuery(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    private <T> Mono<T> error(String userId) {
        return Mono.error(
                () -> new NotFound("User table does not contain user id of: " + userId)
        );
    }

    public Mono<String> findIdGeneric(final String userId) {
        return userRepository.findIdById(userId)
                .switchIfEmpty(error(userId));
    }

    public Mono<Void> checkExistsThrow(final String userId) {
        return userRepository.existsById(userId)
                .filter(Boolean::booleanValue)
                .switchIfEmpty(error(userId))
                .then();
    }

    public Mono<Boolean> checkExists(final String userId) {
        return userRepository.existsById(userId);
    }
}
