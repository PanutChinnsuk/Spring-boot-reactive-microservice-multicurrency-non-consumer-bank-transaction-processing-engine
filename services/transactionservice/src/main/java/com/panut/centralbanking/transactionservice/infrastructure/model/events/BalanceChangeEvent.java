package com.panut.centralbanking.transactionservice.infrastructure.model.events;

import com.panut.centralbanking.appR2dbccore.persistence.VersionedUUIDStringEntity;
import com.panut.centralbanking.transactionservice.api.service.account.data.related.BalanceChangeType;
import com.panut.centralbanking.transactionservice.api.service.account.data.related.BalanceImpact;
import com.panut.centralbanking.transactionservice.core.BalanceChangeEventStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("transaction_balance_change_events")
@Getter
@NoArgsConstructor
public class BalanceChangeEvent extends VersionedUUIDStringEntity {
    @Column("account_id")
    private String accountId;

    @Column("amount")
    private BigDecimal amount;

    @Column("balance_impact")
    private BalanceImpact balanceImpact;

    @Column("balance_change_type")
    private BalanceChangeType balanceChangeType;

    @Column("status")
    private BalanceChangeEventStatus status;

    @Column("transaction_id")
    private String transactionId;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("blacklist_expires_at")
    private LocalDateTime blacklistExpiresAt;

    @Column("applying_expires_at")
    private LocalDateTime applyingExpiresAt;


    public BalanceChangeEvent balanceChangeEvent(String accountId,
                              BigDecimal amount,
                              BalanceImpact balanceImpact,
                              BalanceChangeType balanceChangeType,
                              String transactionId,
                                                 BalanceChangeEventStatus status) {
        this.accountId = accountId;
        this.amount = amount;
        this.balanceImpact = balanceImpact;
        this.balanceChangeType = balanceChangeType;
        this.transactionId = transactionId;
        this.status = status == null
                ? BalanceChangeEventStatus.PENDING
                : status;

        return this;
    }

    public BalanceChangeEvent balanceChangeEvent(String accountId,
                                                 BigDecimal amount,
                                                 BalanceImpact balanceImpact,
                                                 BalanceChangeType balanceChangeType,
                                                 String transactionId) {
        return balanceChangeEvent(accountId, amount, balanceImpact,
                balanceChangeType, transactionId, null);
    }

    @Builder
    public BalanceChangeEvent(String accountId,
                              BigDecimal amount,
                              BalanceImpact balanceImpact,
                              BalanceChangeType balanceChangeType,
                              String transactionId,
                              String id,
                              BalanceChangeEventStatus status) {
        balanceChangeEvent(accountId,
                amount,
                balanceImpact,
                balanceChangeType,
                transactionId,
                status);
        if (id == null) {
            setInsert();
        }else {
            setInsert(id);
        }


    }




}
