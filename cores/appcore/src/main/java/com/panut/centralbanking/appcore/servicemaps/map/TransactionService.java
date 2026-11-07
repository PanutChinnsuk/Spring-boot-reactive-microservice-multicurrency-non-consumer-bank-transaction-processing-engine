package com.panut.centralbanking.appcore.servicemaps.map;

import com.panut.centralbanking.appcore.servicemaps.FullPath;
import com.panut.centralbanking.appcore.servicemaps.ServiceIds;

public class TransactionService {
    private static final String ID = ServiceIds.TRANSACTIONSERVICE;


    public static final String TRANSACTION_CLEAR_PENDING_BALANCE_METHOD = "/clearing-balance/bulk-pending";
    public static final String TRANSACTION_CLEAR_PENDING_BALANCE_FULL_PATH = FullPath.loadBalancedOf(
            ID, TRANSACTION_CLEAR_PENDING_BALANCE_METHOD);




}
