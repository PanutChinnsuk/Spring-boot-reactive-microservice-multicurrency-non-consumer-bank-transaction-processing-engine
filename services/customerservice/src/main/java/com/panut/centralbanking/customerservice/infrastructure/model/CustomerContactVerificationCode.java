package com.panut.centralbanking.customerservice.infrastructure.model;

import com.panut.centralbanking.appR2dbccore.persistence.UUIDBasedStringEntity;
import com.panut.centralbanking.customerservice.core.enums.VerifyingContactType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("customer_contact_verification_codes")
@Getter
@NoArgsConstructor
public class CustomerContactVerificationCode extends UUIDBasedStringEntity {
    @Column("customer_id")
    private String customerId;

    @Column("code")
    private String code;

    @Column("verifying_contact_type")
    private VerifyingContactType verifyingContactType;

    @Column("verifying")
    private String verifying;

    @Column("expires_at")
    private LocalDateTime expiresAt;

    @Builder
    public CustomerContactVerificationCode(String customerId,
                                           String code,
                                           VerifyingContactType verifyingContactType,
                                           String verifying,
                                           LocalDateTime expiresAt) {
        this.customerId = customerId;
        this.code = code;
        this.verifyingContactType = verifyingContactType;
        this.verifying = verifying;
        this.expiresAt = expiresAt;
    }
}
