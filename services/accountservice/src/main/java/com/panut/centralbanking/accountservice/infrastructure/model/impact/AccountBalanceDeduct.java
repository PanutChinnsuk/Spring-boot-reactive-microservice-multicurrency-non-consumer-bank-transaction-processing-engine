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

@Table("account_balance_deducts")
@NoArgsConstructor
@Getter
public class AccountBalanceDeduct extends UUIDBasedStringEntity
        implements BalanceChangeModel {
    @Column("account_id")
    private String accountId;

    @Column("amount")
    private BigDecimal amount;

    @Column("deduct_type")
    private BalanceChangeType balanceChangeType;

    @Column("deducted_at")
    private LocalDateTime deductedAt;

    @Column("transaction_id")
    private String transactionId;


    @Builder
    public AccountBalanceDeduct(String accountId,
                                BigDecimal amount,
                                BalanceChangeType balanceChangeType,
                                LocalDateTime deductedAt,
                                String transactionId) {
        this.accountId = accountId;
        this.amount = amount;
        this.balanceChangeType = balanceChangeType;
        this.deductedAt = deductedAt;
        this.transactionId = transactionId;
        setInsert();
    }

    @Override
    public LocalDateTime getChangedTimestamp() {
        return this.deductedAt;
    }

    @Override
    public Boolean isDeduct() {
        return true;
    }
}
