package com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.policies;

public enum CustomPerAccountFxTransferDataPolicy {
    FOREIGN_PRINCIPAL,
    FOREIGN_BUY_BASE,
    BASE_BUY_FOREIGN;


    public boolean FOREIGN_BUY_BASE() {
        return this == FOREIGN_BUY_BASE;
    }

    public boolean BASE_BUY_FOREIGN() {
        return this == BASE_BUY_FOREIGN;
    }

    public boolean FOREIGN_PRINCIPAL() {
        return this == FOREIGN_PRINCIPAL;
    }
}
