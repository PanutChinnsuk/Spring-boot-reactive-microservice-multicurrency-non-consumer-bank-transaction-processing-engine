package com.panut.centralbanking.transactionservice.core;

public enum TransactionLineDirection {
    CREDIT,
    DEBIT,
    NONE,
    CLEARING_CREDIT,
    CLEARING_DEBIT;

    public boolean isCredit() {
        return this == CREDIT ||
                this == CLEARING_CREDIT;
    }

    public boolean isDebit() {
        return this == DEBIT ||
                this == CLEARING_DEBIT;
    }

    public boolean isNotRequired() {
        return this == NONE;
    }



}
