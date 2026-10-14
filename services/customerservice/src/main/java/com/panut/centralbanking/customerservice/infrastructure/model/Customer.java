package com.panut.centralbanking.customerservice.infrastructure.model;

import com.panut.centralbanking.appR2dbccore.persistence.UUIDBasedStringEntity;
import com.panut.centralbanking.customerservice.core.enums.CustomerStatus;
import lombok.*;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import reactor.core.publisher.Mono;

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

    public Mono<Customer> activateWithUser(String userId) {
       return Mono.defer(() -> {
           if (this.userId != null)
               return Mono.error(() -> new IllegalStateException("Customer's user id " +
                       "is already set."));

           this.userId = userId;
           this.customerStatus = CustomerStatus.ACTIVE_WITH_USER;

           return Mono.just(this);
       });
    }
}
