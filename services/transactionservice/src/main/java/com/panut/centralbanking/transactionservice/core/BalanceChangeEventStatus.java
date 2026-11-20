package com.panut.centralbanking.transactionservice.core;

public enum BalanceChangeEventStatus {
    REVERSED,
    CANCELLED,
    PENDING,
    COMPLETED;


    public boolean isPendingState() {
        return this == PENDING;
    }
}

