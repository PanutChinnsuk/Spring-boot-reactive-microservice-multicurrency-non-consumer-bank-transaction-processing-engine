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

    @Column("paid")
    private BigDecimal paid;

    @Column("status")
    private AccountOverdraftStatus accountOverdraftStatus;

    @Column("transaction_id")
    private String transactionId;

    @Column("overdrafted_at")
    private LocalDateTime overdraftedAt;




    @Builder
    public AccountOverdraft(String accountId,
                            BigDecimal amount,
                            BigDecimal paid,
                            String transactionId,
                            LocalDateTime overdraftedAt) {
        this.accountId = accountId;
        this.amount = amount;
        this.paid = paid == null
                ? BigDecimal.ZERO
                : paid;
        this.transactionId = transactionId;
        this.accountOverdraftStatus = AccountOverdraftStatus.UNPAID;
        this.overdraftedAt = overdraftedAt;
        setInsert();
    }

    public AccountOverdraft paid(final BigDecimal amount) {
        this.paid = this.paid.add(amount);

        if (this.paid.compareTo(this.amount) >= 0)
            this.accountOverdraftStatus = AccountOverdraftStatus.CLEARED;

        return this;
    }


    public AccountOverdraft unpaid(final BigDecimal amount) {
        this.paid = this.paid.subtract(amount);

        if (this.paid.compareTo(this.amount) < 0)
            this.accountOverdraftStatus = AccountOverdraftStatus.UNPAID;

        return this;
    }
}
