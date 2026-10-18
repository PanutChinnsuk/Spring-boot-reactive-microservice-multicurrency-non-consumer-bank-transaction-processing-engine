package com.panut.centralbanking.accountservice.infrastructure.model.impact;

import com.panut.centralbanking.accountservice.core.BalanceChangeType;
import com.panut.centralbanking.appR2dbccore.persistence.UUIDBasedStringEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("account_balance_adds")
@NoArgsConstructor
@Getter
public class AccountBalanceAdd extends UUIDBasedStringEntity
        implements BalanceChangeModel {
    @Column("account_id")
    private String accountId;

    @Column("amount")
    private BigDecimal amount;

    @Column("add_type")
    private BalanceChangeType balanceChangeType;

    @Column("added_at")
    private LocalDateTime addedAt;

    @Column("transaction_id")
    private String transactionId;


    @Builder
    public AccountBalanceAdd(String accountId,
                             BigDecimal amount,
                             BalanceChangeType balanceChangeType,
                             LocalDateTime addedAt,
                             String transactionId) {
        this.accountId = accountId;
        this.amount = amount;
        this.balanceChangeType = balanceChangeType;
        this.addedAt = addedAt;
        this.transactionId = transactionId;
        setInsert();
    }

    @Override
    public LocalDateTime getChangedTimestamp() {
        return this.addedAt;
    }

    @Override
    public Boolean isDeduct() {
        return false;
    }
}
