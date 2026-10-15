package com.panut.centralbanking.userservice.infrastructure.repository;


import com.panut.centralbanking.userservice.infrastructure.model.UserCredentialProfile;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface UserCredentialProfileRepository
        extends ReactiveCrudRepository<UserCredentialProfile, String> {
}
