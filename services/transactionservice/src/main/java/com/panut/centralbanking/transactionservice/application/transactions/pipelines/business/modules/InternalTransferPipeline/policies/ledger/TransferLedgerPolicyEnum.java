package com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.policies.ledger;

public enum TransferLedgerPolicyEnum {
    CONTINUE_TRANSACTION,
    FILL_CLEARING_CREDIT,
    FILL_CLEARING_DEBIT;

    public boolean FILL_CLEARING_CREDIT_OR_DEBIT() {
        return FILL_CLEARING_CREDIT() || FILL_CLEARING_DEBIT();
    }

    public boolean CONTINUE_TRANSACTION() {
        return this == CONTINUE_TRANSACTION;
    }
    public boolean FILL_CLEARING_CREDIT() {
        return this == FILL_CLEARING_CREDIT;
    }
    public boolean FILL_CLEARING_DEBIT() {
        return this == FILL_CLEARING_DEBIT;
    }
}
