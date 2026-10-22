package com.panut.centralbanking.appcore.servicemaps.map;

import com.panut.centralbanking.appcore.servicemaps.FullPath;
import com.panut.centralbanking.appcore.servicemaps.ServiceIds;

public class AccountService {
    private static final String ID = ServiceIds.ACCOUNTSERVICE;


    public static final String BALANCE_CHANGE_PER_TRANSACTION_METHOD = "/balance/per-transaction";
    public static final String BALANCE_CHANGE_PER_TRANSACTION_FULL_PATH = FullPath.loadBalancedOf(
            ID, BALANCE_CHANGE_PER_TRANSACTION_METHOD);


    public static final String ACCOUNTS_STATE_VERIFY_METHOD = "/state/accounts";
    public static final String ACCOUNTS_STATE_VERIFY_FULL_PATH = FullPath.loadBalancedOf(
            ID, ACCOUNTS_STATE_VERIFY_METHOD);


}
