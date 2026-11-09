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

    @Column("overdrafted")
    private BigDecimal overdrafted;


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

    private AccountOverdraftCreateData createOverdraft(final BigDecimal amount,
                                 final AccountBalanceChange change,
                                 final List<AccountOverdraftCreateData>
                                 possibleOverdrafts,
                                 final String accountId,
                                 final BalanceChangeType type) {
        BigDecimal available = getAvailableFromType(type);

        BigDecimal overdrafted = amount.subtract(available);
        String transactionId = change.getTransactionId();

        var overdraftData = AccountOverdraftCreateData
                .createDefault(accountId, overdrafted, transactionId);
        addOverdraftedSimple(overdrafted);

        deductSimple(available, type);

        return overdraftData;
    }

    public List<AccountOverdraftCreateData> orchest(final List<AccountBalanceChange> changes,
                                                        final String accountId) {
        List<AccountOverdraftCreateData> possibleOverdrafts
                = new ArrayList<>();

        changes.forEach(change -> {
            log.info("asc order account balance change orchest {}", change
                    .getBalanceImpact().getAscendingPriority());

            var type = change.getBalanceChangeType();
            var amount = change.getAmount();
            var isOverdraftCycle = type.equals(BalanceChangeType.OVERDRAFTED);
            var isDeduct = change.getBalanceImpact().equals(BalanceImpact.DEDUCT);
            var isAdd = change.getBalanceImpact().equals(BalanceImpact.ADD);

            if (isDeduct && !isOverdraftCycle) {
                boolean isBalanceType = type.equals(BalanceChangeType.BALANCE);
                boolean isReserveType = type.equals(BalanceChangeType.RESERVE);

                boolean isOverdrafted = (isBalanceType && !canDeductBalance(amount)) ||
                        (isReserveType && !canDeductReserve(amount));


                if (isOverdrafted) {

                   var overdraftData = createOverdraft(amount, change, possibleOverdrafts,
                           accountId, type);
                   possibleOverdrafts.add(overdraftData);

                    log.info("Account balance orchest, is overdrafted amount {}",
                            overdraftData.getAmount());
                }else {
                    deductSimple(amount, type);
                }

            }

            //self reminder: seperate cycle in the future
            if (isAdd && !isOverdraftCycle)
                addSimple(amount, type);

            if (isDeduct && isOverdraftCycle)
                deductOverdraftedSimple(amount);

            if (isAdd && isOverdraftCycle)
                addOverdraftedSimple(amount);

        });




        return possibleOverdrafts;
    }

    public Mono<AccountBalance> orchestMono(final List<AccountBalanceChange> changes,
                                            final String accountId) {

        return Mono.defer(() -> {
            orchest(changes, accountId);

            return Mono.just(this);
        });
    }

    public void addOverdraftedSimple(final BigDecimal amount) {
        this.overdrafted = this.overdrafted.add(amount);
        deductLedgerSimple(amount);
    }

    public void deductOverdraftedSimple(final BigDecimal amount) {
        BigDecimal clearing = amount;

        if (amount.compareTo(this.overdrafted) > 0) {

            clearing = this.overdrafted;

            BigDecimal refund = amount.subtract(overdrafted);
            addSimple(refund, BalanceChangeType.BALANCE);

            log.info("Overdraft deducting amount is greater than original, refunded, {}",
                    refund);

        }
        log.info("final overdraft clearing amount, {}", clearing);

        this.overdrafted = this.overdrafted.subtract(clearing);


        addLedgerSimple(clearing);
    }

    public void addLedgerSimple(final BigDecimal amount) {
        this.ledger = this.ledger.add(amount);
    }

    public void deductLedgerSimple(final BigDecimal amount) {
        this.ledger = this.ledger.subtract(amount);
    }

    public void addSimple(final BigDecimal amount,
                                    final BalanceChangeType type) {
        preventModifyingOverdrafted(type);

        if (type.equals(BalanceChangeType.BALANCE)) this.balance = this.balance.add(amount);
        if (type.equals(BalanceChangeType.RESERVE)) this.reserve = this.reserve.add(amount);
        addLedgerSimple(amount);


        preventNegative();
    }

    public void deductSimple(final BigDecimal amount,
                          final BalanceChangeType type) {
        preventModifyingOverdrafted(type);

        if (type.equals(BalanceChangeType.BALANCE)) this.balance = this.balance.subtract(amount);
        if (type.equals(BalanceChangeType.RESERVE)) this.reserve = this.reserve.subtract(amount);
        deductLedgerSimple(amount);

        preventNegative();
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

    private void preventNegative() {
        if (this.balance.signum() < 0)
            throw new IllegalStateException("Account balance can't be negative.");
        if (this.reserve.signum() < 0)
            throw new IllegalStateException("Account reserve can't be negative.");
    }

    private void preventModifyingOverdrafted(final BalanceChangeType type) {
        if (type.equals(BalanceChangeType.OVERDRAFTED))
            throw new SystemMisused("Modifying overdraft can't be use in this operation.");
    }

}
