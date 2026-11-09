package com.panut.centralbanking.accountservice.core;

import lombok.Getter;

@Getter
public enum BalanceImpact {
    DEDUCT(1),
    ADD(2),
    NOTHING(3);

    public boolean add() {
        return this == ADD;
    }

    public boolean deduct() {
        return this == DEDUCT;
    }

    public boolean add_or_deduct() {
        return add() || deduct();
    }


    private int ascendingPriority;

    BalanceImpact(int ascendingPriority) {
        this.ascendingPriority = ascendingPriority;
    }
}
