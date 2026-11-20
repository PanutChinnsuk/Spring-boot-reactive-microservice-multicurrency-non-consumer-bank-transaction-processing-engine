package com.panut.centralbanking.transactionservice.api.service.account.data.related;

public enum BalanceChangeType {
    BALANCE,
    RESERVE,
    OVERDRAFTED;


    public boolean requiredTransactionOverdraftContent() {
        return this == OVERDRAFTED;
    }

    public boolean BALANCE() {
        return this == BALANCE;
    }

    public boolean RESERVE() {
        return this == RESERVE;
    }
}
