package com.panut.centralbanking.userservice.application.userrole.implement;

import com.panut.centralbanking.userservice.application.user.query.UserQuery;
import com.panut.centralbanking.userservice.application.userrole.UserRoleRepositoryManager;
import com.panut.centralbanking.userservice.application.userrole.query.UserRoleService;
import com.panut.centralbanking.userservice.application.userrole.validate.UserRolePersistValidator;
import com.panut.centralbanking.userservice.application.userrolelistelement.query.UserRoleListQuery;
import com.panut.centralbanking.userservice.core.UserRoleEnum;
import com.panut.centralbanking.userservice.infrastructure.model.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class UserRoleServiceImpl implements UserRoleService {
    private final UserRoleRepositoryManager userRoleRepositoryManager;
    private final UserRolePersistValidator userRolePersistValidator;
    private final UserQuery userQuery;
    private final UserRoleListQuery userRoleListQuery;

    public UserRoleServiceImpl(UserRoleRepositoryManager userRoleRepositoryManager,
                               UserRolePersistValidator userRolePersistValidator,
                               UserQuery userQuery,
                               UserRoleListQuery userRoleListQuery) {
        this.userRoleRepositoryManager = userRoleRepositoryManager;
        this.userRolePersistValidator = userRolePersistValidator;
        this.userQuery = userQuery;
        this.userRoleListQuery = userRoleListQuery;
    }


    private Mono<UserRole> insert(final String userId,
                                  final UserRoleEnum userRoleEnum) {
        return  userRoleListQuery.findIdGeneric(userRoleEnum)
                .flatMap(elementId -> userRoleRepositoryManager
                        .save(UserRole.builder()
                                .userId(userId)
                                .userRoleListId(elementId)
                                .build()));
    }

    @Override
    public Mono<UserRole> addRole(final String userId,
                                  final UserRoleEnum userRoleEnum) {
        return userQuery.checkExistsThrow(userId)
                .then(userRolePersistValidator.validateDuplicates(userId, userRoleEnum))
                .then(insert(userId, userRoleEnum))
                .doOnSuccess(a -> log.info("Successfully added user role."))
                .doOnError(error -> log.error("Failed to add user role, {}",
                        error.getLocalizedMessage()));
    }

    @Override
    public Mono<UserRole> addDefaultRole(final String userId) {
        return insert(userId, UserRoleEnum.USER)
                .doOnSuccess(a -> log.info("Successfully added default user role."))
                .doOnError(error -> log.error("Failed to add user role, {}",
                        error.getLocalizedMessage()));
    }
}
