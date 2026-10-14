package com.panut.centralbanking.customerservice.infrastructure.model;

import com.panut.centralbanking.appR2dbccore.persistence.UUIDBasedStringEntity;
import com.panut.centralbanking.appR2dbccore.persistence.VersionedUUIDStringEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("customer_emails")
@Getter
@NoArgsConstructor
public class CustomerEmail extends VersionedUUIDStringEntity {
    @Column("email")
    private String email;

    @Column("verified")
    private Boolean verified;



    @Builder
    public CustomerEmail(String customerId,
                         String email) {
        this.email = email;
        this.verified = false;
        setInsert(customerId);


    }



}
