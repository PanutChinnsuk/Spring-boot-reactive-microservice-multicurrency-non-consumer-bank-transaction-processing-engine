package com.panut.centralbanking.transactionservice.application.ledgeraccount;

import lombok.Getter;

@Getter
public enum LedgerCode {
    COMMON_TRANSFER("COMMON_TRANSFER"),
    FX_TRANSFER("FX_TRANSFER"),
    EXPENSE_TRANSFER("EXPENSE_TRANSFER"),
    ASSET_TRANSFER("ASSET_TRANSFER");

    private final String value;

    LedgerCode(String value) {
        this.value = value;
    }


}
