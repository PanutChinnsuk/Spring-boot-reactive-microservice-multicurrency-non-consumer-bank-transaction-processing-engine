package com.panut.centralbanking.transactionservice.core;

import com.panut.centralbanking.appcore.exceptions.SystemMisused;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum TransactionStatus {
    POSTED(null),
    REVERSED(null),
    CANCELLED(null),

    PENDING_CANCELLED(TransactionStatus.CANCELLED),
    PENDING_POSTED(TransactionStatus.POSTED),
    AUTHORIZED(TransactionStatus.PENDING_POSTED),
    PENDING_AUTHORIZED(TransactionStatus.AUTHORIZED),
    PENDING(TransactionStatus.PENDING_AUTHORIZED)
    ;


    public static Set<TransactionStatus> eventClearableStatuses = Set.of(
            PENDING_AUTHORIZED,
            PENDING_POSTED,
            PENDING_CANCELLED,
            PENDING

    );

    public static Set<TransactionStatus> cancellableStatuses = Arrays
            .stream(TransactionStatus
            .values()).filter(status -> !isAlreadyCancelled(status))
            .collect(Collectors.toSet());



    public static boolean isAlreadyCancelled(final TransactionStatus status) {
        return status == PENDING_CANCELLED ||
                status == CANCELLED;
    }

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
