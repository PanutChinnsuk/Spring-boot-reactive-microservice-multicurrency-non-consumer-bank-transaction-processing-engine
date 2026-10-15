package com.panut.centralbanking.userservice.infrastructure.repository;


import com.panut.centralbanking.userservice.core.UserRoleEnum;
import com.panut.centralbanking.userservice.infrastructure.model.UserRole;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRoleRepository
        extends ReactiveCrudRepository<UserRole, String> {


    @Query("""
            SELECT ur.id FROM user_roles ur
            INNER JOIN user_role_list urle
            ON urle.role_enum = :adding AND
            urle.id = ur.user_role_list_element_id
            WHERE ur.user_id = :userId
            """)
    Mono<String> alreadyExists(@Param("userId") String userId,
                               @Param("adding") UserRoleEnum adding);
}
