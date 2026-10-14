package com.panut.centralbanking.appcore.servicemaps.map;

import com.panut.centralbanking.appcore.servicemaps.FullPath;
import com.panut.centralbanking.appcore.servicemaps.ServiceIds;

public class CustomerService {
    private static final String ID = ServiceIds.CUSTOMERSERVICE;


    public static final String ACTIVATE_USER_METHOD = "/activate-user";
    public static final String ACTIVATE_USER_FULL_PATH = FullPath.loadBalancedOf(
            ID, ACTIVATE_USER_METHOD);

    public static final String GET_ACTIVATION_CODE_METHOD = "/get-activation-user-code";
    public static final String GET_ACTIVATION_CODE_FULL_PATH = FullPath.loadBalancedOf(
            ID, GET_ACTIVATION_CODE_METHOD);
}
