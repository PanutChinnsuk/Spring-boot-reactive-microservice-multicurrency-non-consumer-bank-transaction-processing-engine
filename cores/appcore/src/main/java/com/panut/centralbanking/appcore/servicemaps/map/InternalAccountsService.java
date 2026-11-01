package com.panut.centralbanking.appcore.servicemaps.map;

import com.panut.centralbanking.appcore.servicemaps.FullPath;
import com.panut.centralbanking.appcore.servicemaps.ServiceIds;

public class InternalAccountsService {
    private static final String ID = ServiceIds.ACCOUNTSERVICE;


    public static final String GET_FX_INTERNAL_ACCOUNTS_METHOD = "/fx/get-default";
    public static final String GET_FX_INTERNAL_ACCOUNTS_FULL_PATH = FullPath.loadBalancedOf(
            ID, GET_FX_INTERNAL_ACCOUNTS_METHOD);





}
