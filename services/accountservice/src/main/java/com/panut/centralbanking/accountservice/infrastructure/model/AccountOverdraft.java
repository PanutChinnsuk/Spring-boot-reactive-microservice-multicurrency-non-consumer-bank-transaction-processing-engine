package com.panut.centralbanking.accountservice.infrastructure.model;

import com.panut.centralbanking.accountservice.core.AccountOverdraftStatus;
import com.panut.centralbanking.appR2dbccore.persistence.VersionedUUIDStringEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("account_overdrafts")
@NoArgsConstructor
@Getter
public class AccountOverdraft extends VersionedUUIDStringEntity {
    @Column("account_id")
    private String accountId;

    @Column("amount")
    private BigDecimal amount;

    @Column("status")
    private AccountOverdraftStatus accountOverdraftStatus;

    @Column("overdrafted_at")
    private LocalDateTime overdraftedAt;


    @Builder
    public AccountOverdraft(String accountId,
                            BigDecimal amount,
                            LocalDateTime overdraftedAt) {
        this.accountId = accountId;
        this.amount = amount;
        this.accountOverdraftStatus = AccountOverdraftStatus.UNPAID;
        this.overdraftedAt = overdraftedAt;
        setInsert();
    }
}
