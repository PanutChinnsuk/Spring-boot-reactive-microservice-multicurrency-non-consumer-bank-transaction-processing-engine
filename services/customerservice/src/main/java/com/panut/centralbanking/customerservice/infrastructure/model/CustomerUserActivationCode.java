package com.panut.centralbanking.customerservice.infrastructure.model;

import com.panut.centralbanking.appR2dbccore.persistence.UUIDBasedStringEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Table("customer_user_activation_codes")
@NoArgsConstructor
@Getter
public class CustomerUserActivationCode extends UUIDBasedStringEntity {
    @Column("customer_id")
    private String customerId;

    @Column("code")
    private String code;

    @Column("expires_at")
    private LocalDateTime expiresAt;

    @Column("revoked")
    private Boolean revoked;

    @Version
    @Column("version")
    private Long version;




    @Builder
    public CustomerUserActivationCode(String customerId,
                                      String code,
                                      LocalDateTime expiresAt) {
        this.customerId = customerId;
        this.code = code;
        this.expiresAt = expiresAt;
        this.revoked = false;
        setInsert();
    }


    public Mono<CustomerUserActivationCode> revoke() {
        return Mono.defer(() -> {
            this.revoked = true;
            return Mono.just(this);
        });
    }
}
