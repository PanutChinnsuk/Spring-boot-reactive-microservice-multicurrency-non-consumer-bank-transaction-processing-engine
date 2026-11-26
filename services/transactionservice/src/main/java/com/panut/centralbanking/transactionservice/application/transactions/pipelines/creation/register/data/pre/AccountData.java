package com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre;

import com.panut.centralbanking.transactionservice.api.service.account.response.queryresult.AccountInfoForTransactionQueryResult;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.v1.data.CustomAccountTransferData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.v1.data.CustomAccountTransferDataFxContext;
import com.panut.centralbanking.transactionservice.core.TransactionLineDirection;
import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionLine;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;


@Data
@AllArgsConstructor
public class AccountData {
    private final String objectIdentity = UUID.randomUUID().toString();

    private final String accountId;
    private BigDecimal amount;
    private BigDecimal convertedAmount;
    private final String ledgerAccountId;
    private final TransactionLineDirection direction;

    @Nullable
    private CustomAccountTransferDataFxContext passedTransferDataFxContext;

    public AccountData passContexts(final CustomAccountTransferData customAccountTransferData) {
        this.passedTransferDataFxContext = customAccountTransferData.getFxContext();

        return this;
    }

    public boolean hasPassedTransferDataFxContext() {
        return passedTransferDataFxContext != null &&
                passedTransferDataFxContext.getDirectionForFx().isAppliable();
    }


    public AccountData incrementAmount(final BigDecimal amount) {
        incrementBaseAmount(amount);
        incrementConvertedAmount(amount);

        return this;
    }

    public AccountData incrementConvertedAmount(final BigDecimal amount) {
        this.convertedAmount = this.convertedAmount.add(amount);

        return this;
    }

    public AccountData incrementBaseAmount(final BigDecimal amount) {
        this.amount = this.amount.add(amount);

        return this;
    }

    public AccountData merge(final AccountData accountData) {
        incrementConvertedAmount(accountData.convertedAmount);
        incrementBaseAmount(accountData.amount);

        return this;
    }

    private static AccountData reverse(final TransactionLine line,
                                      final TransactionLineDirection reversedDirection,
                                      final BigDecimal revertedConverted) {
        return new AccountData(line.getAccountId(),
                line.getBasedAmount(),
                revertedConverted,
                line.getLedgerAccountId(),
                reversedDirection,
                null);
    }


    public static AccountData reverseLine(final TransactionLine line,
                                                  final TransactionLineDirection reversedDirection) {
        return reverse(line, reversedDirection, line.getConvertedAmount());
    }

    public static AccountData reverseLine(final TransactionLine line,
                                                  final TransactionLineDirection reversedDirection,
                                                  final BigDecimal revertedConverted) {
        return reverse(line, reversedDirection, revertedConverted);
    }


    public static class V1 {
        public static AccountData accountData(final AccountInfoForTransactionQueryResult accountInfo,
                                      final BigDecimal transferingAmount,
                                      final BigDecimal convertedTransferingAmount,
                                      final String usedLedgerAccountId,
                                      final TransactionLineDirection direction) {
            return new AccountData(accountInfo.getId(),
                    transferingAmount,
                    convertedTransferingAmount,
                    usedLedgerAccountId,
                    direction,
                    null);
        }

        public static AccountData linearCredit(final AccountInfoForTransactionQueryResult accountInfo,
                                               final BigDecimal amount,
                                               final String usedLedgerAccountId) {
            return accountData(accountInfo, amount, amount,
                    usedLedgerAccountId, TransactionLineDirection.CREDIT);
        }

        public static AccountData linearDebit(final AccountInfoForTransactionQueryResult accountInfo,
                                              final BigDecimal amount,
                                              final String usedLedgerAccountId) {
            return accountData(accountInfo, amount, amount,
                    usedLedgerAccountId, TransactionLineDirection.DEBIT);
        }

        public static AccountData linearNone(final AccountInfoForTransactionQueryResult accountInfo,
                                             final BigDecimal amount,
                                             final String usedLedgerAccountId) {
            return accountData(accountInfo, amount, amount,
                    usedLedgerAccountId, TransactionLineDirection.NONE);
        }

        public static AccountData linearClearingDebit(final AccountInfoForTransactionQueryResult accountInfo,
                                             final BigDecimal amount,
                                             final String usedLedgerAccountId) {
            return accountData(accountInfo, amount, amount,
                    usedLedgerAccountId, TransactionLineDirection.CLEARING_DEBIT);
        }

        public static AccountData linearClearingCredit(final AccountInfoForTransactionQueryResult accountInfo,
                                                      final BigDecimal amount,
                                                      final String usedLedgerAccountId) {
            return accountData(accountInfo, amount, amount,
                    usedLedgerAccountId, TransactionLineDirection.CREDIT);
        }


        public static AccountData fxCredit(final AccountInfoForTransactionQueryResult accountInfo,
                                           final BigDecimal amount,
                                           final BigDecimal convertedAmount,
                                           final String usedLedgerAccountId) {
            return accountData(accountInfo, amount, convertedAmount,
                    usedLedgerAccountId, TransactionLineDirection.CREDIT);
        }



        public static AccountData fxDebit(final AccountInfoForTransactionQueryResult accountInfo,
                                          final BigDecimal amount,
                                          final BigDecimal convertedAmount,
                                          final String usedLedgerAccountId) {
            return accountData(accountInfo, amount, convertedAmount,
                    usedLedgerAccountId, TransactionLineDirection.DEBIT);
        }

        public static AccountData fxClearingDebit(final AccountInfoForTransactionQueryResult accountInfo,
                                          final BigDecimal amount,
                                          final BigDecimal convertedAmount,
                                          final String usedLedgerAccountId) {
            return accountData(accountInfo, amount, convertedAmount,
                    usedLedgerAccountId, TransactionLineDirection.CLEARING_DEBIT);
        }

        public static AccountData fxClearingCredit(final AccountInfoForTransactionQueryResult accountInfo,
                                                  final BigDecimal amount,
                                                  final BigDecimal convertedAmount,
                                                  final String usedLedgerAccountId) {
            return accountData(accountInfo, amount, convertedAmount,
                    usedLedgerAccountId, TransactionLineDirection.CLEARING_CREDIT);
        }

        public static AccountData fxNone(final AccountInfoForTransactionQueryResult accountInfo,
                                         final BigDecimal amount,
                                         final BigDecimal convertedAmount,
                                         final String usedLedgerAccountId) {
            return accountData(accountInfo, amount, convertedAmount,
                    usedLedgerAccountId, TransactionLineDirection.NONE);
        }
    }



}

