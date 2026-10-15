package com.panut.centralbanking.customerservice.infrastructure.model;

import com.panut.centralbanking.appR2dbccore.persistence.UUIDBasedStringEntity;
import com.panut.centralbanking.customerservice.core.enums.CustomerTypeEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("customer_type_list")
@Getter
@NoArgsConstructor
public class CustomerTypeListElement extends UUIDBasedStringEntity {
    @Column("type_enum")
    private CustomerTypeEnum customerTypeEnum;


    @Builder
    public CustomerTypeListElement(CustomerTypeEnum customerTypeEnum) {
        this.customerTypeEnum = customerTypeEnum;
        setInsert();
    }
}
