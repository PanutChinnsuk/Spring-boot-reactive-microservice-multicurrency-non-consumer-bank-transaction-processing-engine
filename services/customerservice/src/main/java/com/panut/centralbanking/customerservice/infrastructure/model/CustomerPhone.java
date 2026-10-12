package com.panut.centralbanking.customerservice.infrastructure.model;

import com.panut.centralbanking.appR2dbccore.persistence.UUIDBasedStringEntity;
import com.panut.centralbanking.customerservice.core.enums.CustomerStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("customer_phones")
@Getter
@NoArgsConstructor
public class CustomerPhone extends UUIDBasedStringEntity {
    @Column("phone")
    private String phone;

    @Column("verified")
    private Boolean verified;

    @Version
    @Column("version")
    private Long version;



    @Builder
    public CustomerPhone(String customerId,
                         String phone) {
        this.phone = phone;
        this.verified = false;
        setInsert(customerId);


    }



}
