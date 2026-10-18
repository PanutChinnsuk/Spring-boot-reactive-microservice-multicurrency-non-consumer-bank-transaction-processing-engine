package com.panut.centralbanking.accountservice.infrastructure.model;

import com.panut.centralbanking.accountservice.core.BalanceChangeType;
import com.panut.centralbanking.accountservice.infrastructure.model.impact.BalanceChangeModel;
import com.panut.centralbanking.appR2dbccore.persistence.VersionedUUIDStringEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

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

    public Mono<AccountBalance> orchest(final List<? extends BalanceChangeModel> models) {
        return Mono.defer(() -> {
           models.forEach(model -> {
               if (model.isDeduct()) {
                   deductSimple(model.getAmount(),
                           model.getBalanceChangeType());
               }else {
                   addSimple(model.getAmount(),
                           model.getBalanceChangeType());
               }
           });

           return Mono.just(this);
        });
    }

    public void addLedgerSimple(final BigDecimal amount) {
        this.ledger = this.ledger.add(amount);
    }

    public void deductLedgerSimple(final BigDecimal amount) {
        this.ledger = this.ledger.subtract(amount);
    }

    public void addSimple(final BigDecimal amount,
                                    final BalanceChangeType type) {
        if (type == BalanceChangeType.BALANCE) this.balance = this.balance.add(amount);
        if (type == BalanceChangeType.RESERVE) this.reserve = this.reserve.add(amount);
        addLedgerSimple(amount);
    }

    public void deductSimple(final BigDecimal amount,
                          final BalanceChangeType type) {
        if (type == BalanceChangeType.BALANCE) this.balance = this.balance.subtract(amount);
        if (type == BalanceChangeType.RESERVE) this.reserve = this.reserve.subtract(amount);
        deductLedgerSimple(amount);
    }

    public Mono<AccountBalance> add(final BigDecimal amount,
                                    final BalanceChangeType type) {
        return Mono.defer(() -> {
            addSimple(amount, type);
            return Mono.just(this);
        });
    }


    public Mono<AccountBalance> deduct(final BigDecimal amount,
                                       final BalanceChangeType type) {
        return Mono.defer(() -> {
            deductSimple(amount, type);
            return Mono.just(this);
        });
    }


    public Mono<AccountBalance> addLedger(final BigDecimal amount) {
        return Mono.defer(() -> {
            addLedgerSimple(amount);
            return Mono.just(this);
        });
    }

    public Mono<AccountBalance> deductLedger(final BigDecimal amount) {
        return Mono.defer(() -> {
            deductLedgerSimple(amount);
            return Mono.just(this);
        });
    }

}
