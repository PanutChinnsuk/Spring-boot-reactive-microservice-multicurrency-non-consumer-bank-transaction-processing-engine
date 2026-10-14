package com.panut.centralbanking.userservice.infrastructure.model;

import com.panut.centralbanking.appR2dbccore.persistence.*;
import com.panut.centralbanking.userservice.core.UserStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("users")
@Getter
@NoArgsConstructor
public class User extends VersionedUUIDStringEntity {
    @Column("status")
    private UserStatus userStatus;

    @Column("created_at")
    private LocalDateTime createdAt;




    @Builder
    public User(UserStatus userStatus) {
        this.userStatus = userStatus;
        setInsert();
    }

}
