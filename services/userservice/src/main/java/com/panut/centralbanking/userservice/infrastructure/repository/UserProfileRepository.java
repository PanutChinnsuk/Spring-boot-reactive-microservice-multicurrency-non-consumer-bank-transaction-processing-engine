package com.panut.centralbanking.userservice.infrastructure.repository;


import com.panut.centralbanking.userservice.infrastructure.model.UserProfile;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface UserProfileRepository
        extends ReactiveCrudRepository<UserProfile, String> {
}
