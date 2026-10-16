package com.panut.centralbanking.userservice.infrastructure.model;

import com.panut.centralbanking.appR2dbccore.persistence.UUIDBasedStringEntity;
import com.panut.centralbanking.userservice.core.UserRoleEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("user_roles")
@Getter
@NoArgsConstructor
public class UserRole extends UUIDBasedStringEntity {
    @Column("user_id")
    private String userId;

    @Column("user_role_list_element_id")
    private String userRoleListElementId;

    @Builder
    public UserRole(String userId,
                    String userRoleListElementId) {
        this.userId = userId;
        this.userRoleListElementId = userRoleListElementId;
        setInsert();
    }
}
