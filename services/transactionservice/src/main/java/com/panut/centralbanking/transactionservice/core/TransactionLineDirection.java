package com.panut.centralbanking.transactionservice.core;

public enum TransactionLineDirection {
    CREDIT,
    DEBIT,
    NONE,
    CLEARING_CREDIT,
    CLEARING_DEBIT;

    public boolean isCredit() {
        return this == CREDIT;
    }

    public boolean isDebit() {
        return this == DEBIT;
    }

    public boolean isCreditSide() {
        return this == CREDIT ||
                this == CLEARING_CREDIT;
    }

    public boolean isDebitSide() {
        return this == DEBIT ||
                this == CLEARING_DEBIT;
    }

    public boolean isNotRequired() {
        return this == NONE;
    }



}
