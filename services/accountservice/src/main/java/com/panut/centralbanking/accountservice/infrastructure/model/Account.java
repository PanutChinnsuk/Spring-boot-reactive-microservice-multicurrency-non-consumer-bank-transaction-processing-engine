package com.panut.centralbanking.accountservice.infrastructure.model;

import com.panut.centralbanking.appR2dbccore.persistence.UUIDBasedStringEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("accounts")
@Getter
@NoArgsConstructor
public class Account extends UUIDBasedStringEntity {
    @Column("customer_id")
    private String customerId;

    @Column("created_at")
    private LocalDateTime createdAt;


    @Builder
    public Account(String customerId) {
        this.customerId = customerId;
        setInsert();
    }
}
