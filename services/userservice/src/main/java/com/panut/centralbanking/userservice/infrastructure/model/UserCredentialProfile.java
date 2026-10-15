package com.panut.centralbanking.userservice.infrastructure.model;

import com.panut.centralbanking.appR2dbccore.persistence.VersionedUUIDStringEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("user_credential_profiles")
@Getter
@NoArgsConstructor
public class UserCredentialProfile extends VersionedUUIDStringEntity {
    @Column("password_hash")
    private String passwordHash;

    @Column("pin_hash")
    private String pinHash;

    @Column("recovery_password_hash")
    private String recoveryPasswordHash;




    @Builder
    public UserCredentialProfile(String userId,
                                 String passwordHash) {
        this.passwordHash = passwordHash;
        setInsert(userId);
    }

}
