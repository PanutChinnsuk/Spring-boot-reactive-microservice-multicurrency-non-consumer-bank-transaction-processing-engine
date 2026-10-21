package com.panut.centralbanking.userservice.infrastructure.repository;

import com.panut.centralbanking.userservice.infrastructure.model.User;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User, String> {



    @Query("""
            SELECT u.id FROM users u
            WHERE u.id = :id
            """)
    Mono<String> findIdById(@Param("id") String id);




}
