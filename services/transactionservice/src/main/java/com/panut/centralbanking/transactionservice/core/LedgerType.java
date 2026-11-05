package com.panut.centralbanking.transactionservice.core;

public enum LedgerType {
    LIABILITY,
    EQUITY,
    REVENUE,

    EXPENSE,
    ASSET;


    public boolean isAssetExpense() {
        return this == EXPENSE ||
                this == ASSET;
    }


}
