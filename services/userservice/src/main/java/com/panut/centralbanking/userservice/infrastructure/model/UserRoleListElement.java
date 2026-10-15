package com.panut.centralbanking.userservice.infrastructure.model;

import com.panut.centralbanking.appR2dbccore.persistence.UUIDBasedStringEntity;
import com.panut.centralbanking.userservice.core.UserRoleEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("user_role_list")
@Getter
@NoArgsConstructor
public class UserRoleListElement extends UUIDBasedStringEntity {
    @Column("role_enum")
    private UserRoleEnum userRoleEnum;

    @Builder
    public UserRoleListElement(UserRoleEnum userRoleEnum) {
        this.userRoleEnum = userRoleEnum;
        setInsert();
    }
}
