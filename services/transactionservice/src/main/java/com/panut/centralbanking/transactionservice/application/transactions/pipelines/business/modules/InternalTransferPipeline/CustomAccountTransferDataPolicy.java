package com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline;

public enum CustomAccountTransferDataPolicy {
    ACTIVE_ACCOUNTS_ONLY;


    public boolean ACTIVE_ACCOUNTS_ONLY() {
        return this == ACTIVE_ACCOUNTS_ONLY;
    }


}
