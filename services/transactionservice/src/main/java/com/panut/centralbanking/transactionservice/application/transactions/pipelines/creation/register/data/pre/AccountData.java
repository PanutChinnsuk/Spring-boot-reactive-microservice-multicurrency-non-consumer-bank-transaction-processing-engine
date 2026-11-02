package com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre;

import com.panut.centralbanking.transactionservice.api.service.account.response.queryresult.AccountInfoForTransactionQueryResult;
import com.panut.centralbanking.transactionservice.core.TransactionLineDirection;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class AccountData {
    private final String accountId;
    private final BigDecimal amount;
    private final BigDecimal convertedAmount;
    private final String ledgerAccountId;
    private final TransactionLineDirection direction;


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
                    direction);
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

