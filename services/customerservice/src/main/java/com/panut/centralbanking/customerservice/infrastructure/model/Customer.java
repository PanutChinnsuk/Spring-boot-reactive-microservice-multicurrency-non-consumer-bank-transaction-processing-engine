package com.panut.centralbanking.customerservice.infrastructure.model;

import com.panut.centralbanking.appR2dbccore.persistence.UUIDBasedStringEntity;
import com.panut.centralbanking.customerservice.core.enums.CustomerStatus;
import lombok.*;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("customers")
@Getter
@NoArgsConstructor
public class Customer extends UUIDBasedStringEntity {
    @Column("user_id")
    private String userId;

    @Column("status")
    private CustomerStatus customerStatus;

    @Version
    @Column("version")
    private Long version;


    @Builder
    public Customer(String userId,
                    CustomerStatus customerStatus) {
        this.userId = userId;
        this.customerStatus = customerStatus;
        setInsert();
    }
}
