package com.panut.centralbanking.customerservice.api.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.panut.centralbanking.customerservice.core.enums.CustomerStatus;
import com.panut.centralbanking.customerservice.core.enums.Gender;
import com.panut.centralbanking.customerservice.infrastructure.model.*;
import com.thoughtworks.xstream.core.util.CustomObjectInputStream;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Column;
import reactor.core.publisher.Mono;

@Data
public class CustomerCreatedData {
    @JsonIgnore
    private String customerId;

    private String userId;
    private CustomerStatus customerStatus;

    @JsonIgnore
    private String emailParentId;

    private String email;
    private Boolean verifiedEmail;

    @JsonIgnore
    private String phoneParentId;

    private String phone;
    private Boolean verifiedPhone;

    @JsonIgnore
    private String profileParentId;

    private String firstname;
    private String lastname;
    private String address;
    private Integer age;
    private Gender gender;

    private String customerTypeId;
    private String customerTypeParentId;

    @JsonIgnore
    private String customerUserActivationCodeId;

    private String customerUserActivationCode;


    @JsonIgnore
    public Mono<CustomerCreatedData> customer(Customer customer) {
        return Mono.defer(() -> {
            String userId = customer.getUserId();
            this.userId = userId == null
                    ? "NO USER"
                    : userId;
            this.customerStatus = customer.getCustomerStatus();
            this.customerId = customer.getId();
            return Mono.just(this);
        });

    }

    @JsonIgnore
    public Mono<CustomerCreatedData> customerEmail(CustomerEmail customerEmail) {
        return Mono.defer(() -> {
            this.email = customerEmail.getEmail();
            this.verifiedEmail = customerEmail.getVerified();
            this.emailParentId = customerEmail.getId();

            return Mono.just(this);
        });
    }

    @JsonIgnore
    public Mono<CustomerCreatedData> customerPhone(CustomerPhone customerPhone) {
        return Mono.defer(() -> {
            this.phone = customerPhone.getPhone();
            this.verifiedPhone = customerPhone.getVerified();
            this.phoneParentId = customerPhone.getId();

            return Mono.just(this);
        });
    }

    @JsonIgnore
    public Mono<CustomerCreatedData> customerProfile(CustomerProfile customerProfile) {
        return Mono.defer(() -> {
            this.profileParentId = customerProfile.getId();
            this.firstname = customerProfile.getFirstname();
            this.lastname = customerProfile.getLastname();
            this.address = customerProfile.getAddress();
            this.age = customerProfile.getAge();;
            this.gender = customerProfile.getGender();

            return Mono.just(this);
        });
    }

    @JsonIgnore
    public Mono<CustomerCreatedData> customerType(CustomerType customerType) {
        return Mono.defer(() -> {
            this.customerTypeId = customerType.getId();
            this.customerTypeParentId = customerType.getCustomerId();

            return Mono.just(this);
        });
    }

    @JsonIgnore
    public Mono<CustomerCreatedData> customerUserActivationCode
            (CustomerUserActivationCode customerUserActivationCode) {
        return Mono.defer(() -> {
            this.customerUserActivationCodeId =
                    customerUserActivationCode.getId();
            this.customerUserActivationCode =
                    customerUserActivationCode.getCode();

            return Mono.just(this);
        });
    }

}
