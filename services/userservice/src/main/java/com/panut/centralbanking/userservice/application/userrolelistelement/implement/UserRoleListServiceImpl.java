package com.panut.centralbanking.userservice.application.userrolelistelement.implement;

import com.panut.centralbanking.userservice.application.userrolelistelement.UserRoleListElementRepositoryManager;
import com.panut.centralbanking.userservice.application.userrolelistelement.query.UserRoleListService;
import com.panut.centralbanking.userservice.application.userrolelistelement.validate.UserRoleListPersistValidator;
import com.panut.centralbanking.userservice.core.UserRoleEnum;
import com.panut.centralbanking.userservice.infrastructure.model.UserRoleListElement;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserRoleListServiceImpl implements UserRoleListService {
    private final UserRoleListPersistValidator userRoleListPersistValidator;
    private final UserRoleListElementRepositoryManager userRoleListElementRepositoryManager;

    public UserRoleListServiceImpl(UserRoleListPersistValidator userRoleListPersistValidator,
                                   UserRoleListElementRepositoryManager userRoleListElementRepositoryManager) {
        this.userRoleListPersistValidator = userRoleListPersistValidator;
        this.userRoleListElementRepositoryManager = userRoleListElementRepositoryManager;
    }


    @Override
    public Mono<UserRoleListElement> add(final UserRoleEnum userRoleEnum) {
        return userRoleListPersistValidator.validateDuplicates(userRoleEnum)
                .then(userRoleListElementRepositoryManager.save(
                        UserRoleListElement.builder()
                                .userRoleEnum(userRoleEnum)
                                .build()
                ));
    }
}
