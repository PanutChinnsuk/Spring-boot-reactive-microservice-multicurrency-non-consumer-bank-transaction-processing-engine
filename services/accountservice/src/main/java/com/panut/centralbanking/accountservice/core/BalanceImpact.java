package com.panut.centralbanking.accountservice.core;

import lombok.Getter;

@Getter
public enum BalanceImpact {
    DEDUCT(1),
    ADD(2),
    NOTHING(3);

    private int ascendingPriority;

    BalanceImpact(int ascendingPriority) {
        this.ascendingPriority = ascendingPriority;
    }
}
