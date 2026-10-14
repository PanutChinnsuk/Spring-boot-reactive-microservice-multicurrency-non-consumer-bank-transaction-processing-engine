package com.panut.centralbanking.customerservice.infrastructure.model;

import com.panut.centralbanking.appR2dbccore.persistence.UUIDBasedStringEntity;
import com.panut.centralbanking.appR2dbccore.persistence.VersionedUUIDStringEntity;
import com.panut.centralbanking.customerservice.core.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("customer_profiles")
@NoArgsConstructor
@Getter
public class CustomerProfile extends VersionedUUIDStringEntity {
    @Column("firstname")
    private String firstname;

    @Column("lastname")
    private String lastname;

    @Column("address")
    private String address;

    @Column("age")
    private Integer age;

    @Column("gender")
    private Gender gender;

    @Column("citizen_id")
    private String citizenId;



    @Builder
    public CustomerProfile(String customerId,
                           String firstname,
                           String lastname,
                           String address,
                           Integer age,
                           Gender gender,
                           String citizenId) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.address = address;
        this.age = age;
        this.gender = gender;
        this.citizenId = citizenId;
        setInsert(customerId);
    }
}
