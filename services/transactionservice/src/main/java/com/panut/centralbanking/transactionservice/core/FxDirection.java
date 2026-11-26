package com.panut.centralbanking.transactionservice.core;

public enum FxDirection {
    BID,
    ASK,
    MID,
    TREASURY,
    MULTI,
    NONE;


    public boolean isAppliable() {
        return this != MULTI &&
                this != NONE;
    }
}
