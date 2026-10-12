package com.panut.centralbanking.customerservice.infrastructure.model;

import com.panut.centralbanking.appR2dbccore.persistence.UUIDBasedStringEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("customer_emails")
@Getter
@NoArgsConstructor
public class CustomerEmail extends UUIDBasedStringEntity {
    @Column("email")
    private String email;

    @Column("verified")
    private Boolean verified;

    @Version
    @Column("version")
    private Long version;



    @Builder
    public CustomerEmail(String customerId,
                         String email) {
        this.email = email;
        this.verified = false;
        setInsert(customerId);


    }



}
