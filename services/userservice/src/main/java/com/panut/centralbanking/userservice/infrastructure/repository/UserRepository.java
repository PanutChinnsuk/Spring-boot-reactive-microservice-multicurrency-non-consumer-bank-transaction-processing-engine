package com.panut.centralbanking.userservice.infrastructure.repository;

import com.panut.centralbanking.userservice.infrastructure.model.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface UserRepository extends ReactiveCrudRepository<User, String> {
}
