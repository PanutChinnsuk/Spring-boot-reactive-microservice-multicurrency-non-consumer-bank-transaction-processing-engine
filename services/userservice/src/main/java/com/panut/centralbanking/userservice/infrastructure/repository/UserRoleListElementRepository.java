package com.panut.centralbanking.userservice.infrastructure.repository;


import com.panut.centralbanking.userservice.core.UserRoleEnum;
import com.panut.centralbanking.userservice.infrastructure.model.UserRoleListElement;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRoleListElementRepository
        extends ReactiveCrudRepository<UserRoleListElement, String> {

    Mono<Boolean> existsByUserRoleEnum(UserRoleEnum userRoleEnum);

    @Query("""
            SELECT e.id FROM user_role_list e
            WHERE e.role_enum = :userRoleEnum
            """)
    Mono<String> findIdByUserRoleEnum(@Param("userRoleEnum") UserRoleEnum userRoleEnum);
}
