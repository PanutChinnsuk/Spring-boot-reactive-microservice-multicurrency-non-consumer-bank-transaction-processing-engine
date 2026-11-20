package com.panut.centralbanking.transactionservice.api.service.account.data.related;

public enum BalanceImpact {
    NOTHING,
    DEDUCT,
    ADD;

    public boolean ADD() {
        return this == ADD;
    }

    public boolean DEDUCT() {
        return this == DEDUCT;
    }



}
