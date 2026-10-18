package com.panut.centralbanking.accountservice.infrastructure.model;

import com.panut.centralbanking.accountservice.core.AccountStatus;
import com.panut.centralbanking.accountservice.core.AccountType;

import com.panut.centralbanking.appR2dbccore.persistence.VersionedUUIDStringEntity;
import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("account_profiles")
@Getter
@NoArgsConstructor
public class AccountProfile extends VersionedUUIDStringEntity {
    @Column("currency")
    private CurrencyCode accountCurrency;

    @Column("account_type")
    private AccountType accountType;

    @Column("status")
    private AccountStatus accountStatus;


    @Builder
    public AccountProfile(String accountId,
                          CurrencyCode accountCurrency,
                          AccountType accountType) {
        this.accountCurrency = accountCurrency;
        this.accountType = accountType;
        this.accountStatus = AccountStatus.ACTIVE;
        setInsert(accountId);
    }
}
