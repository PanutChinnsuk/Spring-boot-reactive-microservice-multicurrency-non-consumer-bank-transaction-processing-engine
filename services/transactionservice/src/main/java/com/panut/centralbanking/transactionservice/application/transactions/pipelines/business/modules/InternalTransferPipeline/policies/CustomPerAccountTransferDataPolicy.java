package com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.policies;

public enum CustomPerAccountTransferDataPolicy {
    IS_BASE_CURRENCY,
    IS_FOREIGN_CURRENCY,
    IS_INTERNAL;

    public boolean IS_BASE_CURRENCY() {
        return this == IS_BASE_CURRENCY;
    }

    public boolean IS_FOREIGN_CURRENCY() {
        return this == IS_FOREIGN_CURRENCY;
    }

    public boolean IS_INTERNAL() {
        return this == IS_INTERNAL;
    }
}
