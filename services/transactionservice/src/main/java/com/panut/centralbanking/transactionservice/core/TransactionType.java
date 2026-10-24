package com.panut.centralbanking.transactionservice.core;

public enum TransactionType {
    TRANSFER,
    ASSET_EXPENSE,
    FX_TRANSFER,
    CLEARING,
    INTERNAL;


    public boolean isNormalTransfer() {
        return this == TRANSFER;
    }

    public boolean isAssetExpense() {
        return this == ASSET_EXPENSE;
    }

    public boolean isFxTransfer() {
        return this == FX_TRANSFER;
    }

    public boolean isClearing() {
        return this == CLEARING;
    }

    public boolean isInternal() {
        return this == INTERNAL;
    }
}
