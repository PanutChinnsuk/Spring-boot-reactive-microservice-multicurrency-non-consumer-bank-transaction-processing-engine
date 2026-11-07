package com.panut.centralbanking.accountservice.infrastructure.model;

import com.panut.centralbanking.accountservice.api.data.AccountOverdraftCreateData;
import com.panut.centralbanking.accountservice.core.BalanceChangeType;
import com.panut.centralbanking.accountservice.core.BalanceImpact;
import com.panut.centralbanking.appR2dbccore.persistence.VersionedUUIDStringEntity;
import com.panut.centralbanking.appcore.exceptions.SystemMisused;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
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

    public BigDecimal getAvailableFromType(final BalanceChangeType type) {
        return switch (type) {
            case BALANCE -> this.balance;
            case RESERVE -> this.reserve;
            default -> throw new SystemMisused("Balance change type not " +
                    "allowed in getAvailableFromType");
        };
    }

    private void createOverdraft(final BigDecimal amount,
                                 final AccountBalanceChange change,
                                 final Map<String, List<AccountOverdraftCreateData>>
                                 possibleOverdrafts,
                                 final String accountId,
                                 final BalanceChangeType type) {
        BigDecimal available = getAvailableFromType(type);

        BigDecimal overdrafted = amount.subtract(available);
        String transactionId = change.getTransactionId();

        var overdraftList = possibleOverdrafts.computeIfAbsent(accountId,
                id -> new ArrayList<>());

        var overdraftData = AccountOverdraftCreateData
                .createDefault(accountId, overdrafted, transactionId);

        overdraftList.add(overdraftData);

        possibleOverdrafts.put(accountId, overdraftList);


        deductSimple(available, type);
    }

    public Optional<Map<String,List<AccountOverdraftCreateData>>> orchest(final List<AccountBalanceChange> changes,
                                                        final String accountId) {
        Map<String,List<AccountOverdraftCreateData>> possibleOverdrafts
                = new HashMap<>();

        changes.forEach(change -> {
            log.info("asc order account balance change orchest {}", change
                    .getBalanceImpact().getAscendingPriority());

            var type = change.getBalanceChangeType();
            var amount = change.getAmount();
            if (change.getBalanceImpact().equals(BalanceImpact.DEDUCT)) {
                boolean isBalanceType = type.equals(BalanceChangeType.BALANCE);
                boolean isReserveType = type.equals(BalanceChangeType.RESERVE);

                boolean isOverdrafted = (isBalanceType && !canDeductBalance(amount)) ||
                        (isReserveType && !canDeductReserve(amount));


                if (isOverdrafted) {
                   createOverdraft(amount, change, possibleOverdrafts,
                           accountId, type);
                }else {
                    deductSimple(amount, type);
                }

            }

            if (change.getBalanceImpact().equals(BalanceImpact.ADD)) {
                addSimple(amount, type);
            }

        });




        return possibleOverdrafts.isEmpty()
                ? Optional.empty()
                : Optional.of(possibleOverdrafts);
    }

    public Mono<AccountBalance> orchestMono(final List<AccountBalanceChange> changes,
                                            final String accountId) {

        return Mono.defer(() -> {
            orchest(changes, accountId);

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

        forcePreventNegative();
    }

    public void deductSimple(final BigDecimal amount,
                          final BalanceChangeType type) {
        if (type == BalanceChangeType.BALANCE) this.balance = this.balance.subtract(amount);
        if (type == BalanceChangeType.RESERVE) this.reserve = this.reserve.subtract(amount);



        deductLedgerSimple(amount);

        forcePreventNegative();
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


    public boolean canSafeApply(final BigDecimal balance,
                                final BigDecimal reserve) {
        return canDeductBalance(balance) &&
                canDeductReserve(reserve);
    }


    private boolean canDeductBalance(final BigDecimal amount) {
        return this.balance.compareTo(amount) >= 0;
    }

    private boolean canDeductReserve(final BigDecimal amount) {
        return this.reserve.compareTo(amount) >= 0;
    }

    private void forcePreventNegative() {
        if (this.balance.signum() < 0)
            throw new IllegalStateException("Account balance can't be negative.");
        if (this.reserve.signum() < 0)
            throw new IllegalStateException("Account reserve can't be negative.");
    }

}
