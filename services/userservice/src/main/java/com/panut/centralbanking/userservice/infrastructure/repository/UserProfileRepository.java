package com.panut.centralbanking.userservice.infrastructure.repository;


import com.panut.centralbanking.userservice.infrastructure.model.UserProfile;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserProfileRepository
        extends ReactiveCrudRepository<UserProfile, String> {


    Mono<Boolean> existsByUsername(String username);
}
