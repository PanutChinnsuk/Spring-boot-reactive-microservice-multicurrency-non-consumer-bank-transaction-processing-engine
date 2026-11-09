package com.panut.centralbanking.accountservice.infrastructure.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.panut.centralbanking.accountservice.api.data.context.OverdraftChangeContext;
import com.panut.centralbanking.accountservice.core.BalanceChangeType;
import com.panut.centralbanking.accountservice.core.BalanceImpact;
import com.panut.centralbanking.appR2dbccore.persistence.UUIDBasedStringEntity;
import com.panut.centralbanking.appcore.exceptions.SystemMisused;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("account_balance_changes")
@NoArgsConstructor
@Getter
public class AccountBalanceChange extends UUIDBasedStringEntity {
    @Column("account_id")
    private String accountId;

    @Column("amount")
    private BigDecimal amount;

    @Column("change_type")
    private BalanceChangeType balanceChangeType;

    @Column("changed_at")
    private LocalDateTime changedAt;

    @Column("balance_impact")
    private BalanceImpact balanceImpact;

    @Column("transaction_id")
    private String transactionId;


    @Builder
    public AccountBalanceChange(String accountId,
                                BigDecimal amount,
                                BalanceChangeType balanceChangeType,
                                LocalDateTime changedAt,
                                BalanceImpact balanceImpact,
                                String transactionId) {
        this.accountId = accountId;
        this.amount = amount;
        this.balanceChangeType = balanceChangeType;
        this.changedAt = changedAt;
        this.balanceImpact = balanceImpact;
        this.transactionId = transactionId;
        setInsert();
    }


    @Transient
    private OverdraftChangeContext overdraftChangeContext;


    public boolean hasOverdraftChangeContext() {
        return this.overdraftChangeContext != null;
    }


    public AccountBalanceChange withOverdraftChangeContext(final OverdraftChangeContext ctx) {
        if (hasOverdraftChangeContext())
            throw new SystemMisused("AccountBalanceChange overdraftChangeContext " +
                    "can only be passed once.");

        this.overdraftChangeContext = ctx;


        return this;
    }


}
