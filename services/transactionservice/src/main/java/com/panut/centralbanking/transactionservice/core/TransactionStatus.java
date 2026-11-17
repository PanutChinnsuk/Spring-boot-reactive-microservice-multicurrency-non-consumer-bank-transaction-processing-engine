package com.panut.centralbanking.transactionservice.core;

import com.panut.centralbanking.appcore.exceptions.SystemMisused;

public enum TransactionStatus {
    POSTED(null),
    REVERSED(null),
    CANCELLED(null),

    PENDING_POSTED(TransactionStatus.POSTED),
    AUTHORIZED(TransactionStatus.PENDING_POSTED),
    PENDING_AUTHORIZED(TransactionStatus.AUTHORIZED),
    PENDING(TransactionStatus.PENDING_AUTHORIZED),


    PENDING_REVERSED(TransactionStatus.REVERSED),
    ;



    TransactionStatus upnextStatus;

    TransactionStatus(TransactionStatus upnextStatus) {
        this.upnextStatus = upnextStatus;
    }

    public TransactionStatus getUpnextStatus() {
        if (upnextStatus == null)
            throw new SystemMisused("Current transaction status of " + this
            + " does not have up next status.");

        return upnextStatus;
    }


}
