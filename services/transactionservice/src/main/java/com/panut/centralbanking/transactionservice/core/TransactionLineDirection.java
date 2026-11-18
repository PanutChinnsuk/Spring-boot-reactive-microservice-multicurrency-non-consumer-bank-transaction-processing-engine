package com.panut.centralbanking.transactionservice.core;

public enum TransactionLineDirection {
    CREDIT,
    DEBIT,
    CREDIT_OVERDRAFT,
    DEBIT_OVERDRAFT,
    NONE,
    CLEARING_CREDIT,
    CLEARING_DEBIT;

    public boolean isCredit() {
        return this == CREDIT;
    }

    public boolean isDebit() {
        return this == DEBIT;
    }

    public boolean isCreditOverdraft() {
        return this == CREDIT_OVERDRAFT;
    }

    public boolean isDebitOverdraft() {
        return this == DEBIT_OVERDRAFT;
    }

    public boolean isClearingCredit() {
        return this == CLEARING_CREDIT;
    }

    public boolean isClearingDebit() {
        return this == CLEARING_DEBIT;
    }

    public boolean isClearingSide() {
        return isClearingCredit() || isClearingDebit();
    }

    public boolean isCreditSide() {
        return this == CREDIT ||
                this == CLEARING_CREDIT ||
                this == CREDIT_OVERDRAFT;
    }

    public boolean isDebitSide() {
        return this == DEBIT ||
                this == CLEARING_DEBIT ||
                this == DEBIT_OVERDRAFT;
    }

    public boolean isNotRequired() {
        return this == NONE;
    }



}
