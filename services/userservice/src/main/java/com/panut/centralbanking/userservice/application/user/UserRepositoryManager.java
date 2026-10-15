package com.panut.centralbanking.userservice.application.user;

import com.panut.centralbanking.userservice.infrastructure.model.User;
import com.panut.centralbanking.userservice.infrastructure.repository.UserRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class UserRepositoryManager {
    private final UserRepository userRepository;

    public UserRepositoryManager(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<User> save(final User user) {
        return this.userRepository.save(user);
    }

}
