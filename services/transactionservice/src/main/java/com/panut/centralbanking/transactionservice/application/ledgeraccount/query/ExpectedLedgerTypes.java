package com.panut.centralbanking.transactionservice.application.ledgeraccount.query;

import com.panut.centralbanking.transactionservice.core.LedgerType;

import java.util.Set;

public class ExpectedLedgerTypes {



    public static Set<LedgerType> ALL() {
        return Set.of(LedgerType.values());
    }

    public static Set<LedgerType> NORMAL_TRANSFER() {
        return Set.of(LedgerType.LIABILITY,
                LedgerType.REVENUE,
                LedgerType.EQUITY);
    }

    public static Set<LedgerType> ASSET_AND_EXPENSE() {
        return Set.of(LedgerType.ASSET,
                LedgerType.EXPENSE);
    }

    public static Set<LedgerType> WITH(final LedgerType... ledgerTypes) {
        return Set.of(ledgerTypes);
    }


}
