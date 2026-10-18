package com.panut.centralbanking.accountservice.infrastructure.model;

import com.panut.centralbanking.appR2dbccore.persistence.VersionedUUIDStringEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("account_balances")
@NoArgsConstructor
@Getter
public class AccountBalance extends VersionedUUIDStringEntity {
    @Column("balance")
    private BigDecimal balance;

    @Column("reserve")
    private BigDecimal reserve;

    @Column("ledger")
    private BigDecimal ledger;


    @Builder
    public AccountBalance(String accountId,
                          BigDecimal balance) {
        this.balance = balance;
        this.reserve = BigDecimal.ZERO;
        this.ledger = balance.add(reserve);
        setInsert(accountId);
    }
}
