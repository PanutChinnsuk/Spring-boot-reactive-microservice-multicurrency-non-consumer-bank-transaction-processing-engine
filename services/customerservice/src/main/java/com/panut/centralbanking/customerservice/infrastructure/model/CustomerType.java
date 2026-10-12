package com.panut.centralbanking.customerservice.infrastructure.model;

import com.panut.centralbanking.appR2dbccore.persistence.UUIDBasedStringEntity;
import com.panut.centralbanking.customerservice.core.enums.CustomerTypeEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("customer_types")
@Getter
@NoArgsConstructor
public class CustomerType extends UUIDBasedStringEntity {
    @Column("customer_id")
    private String customerId;

    @Column("customer_type_list_id")
    private String customerTypeListId;


    @Builder
    public CustomerType(String customerId,
                        String customerTypeListId) {
        this.customerId = customerId;
        this.customerTypeListId = customerTypeListId;
        setInsert();
    }
}
