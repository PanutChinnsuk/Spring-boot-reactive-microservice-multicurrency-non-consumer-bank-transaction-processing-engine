package com.panut.centralbanking.userservice.infrastructure.model;

import com.panut.centralbanking.appR2dbccore.persistence.VersionedUUIDStringEntity;
import com.panut.centralbanking.userservice.core.UserStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Arrays;

@Table("user_profiles")
@Getter
@NoArgsConstructor
public class UserProfile extends VersionedUUIDStringEntity {
    @Column("status")
    private String username;






    @Builder
    public UserProfile(String userId,
                       String username) {
        this.username = username;
        setInsert(userId);
    }

}
