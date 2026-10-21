package com.panut.centralbanking.appcore.servicemaps.map;

import com.panut.centralbanking.appcore.servicemaps.FullPath;
import com.panut.centralbanking.appcore.servicemaps.ServiceIds;

public class FxRateService {
    private static final String ID = ServiceIds.FXRATESERVICE;


    public static final String GET_LATEST_METHOD = "/get-latest";
    public static final String GET_LATEST_FULL_PATH = FullPath.loadBalancedOf(
            ID, GET_LATEST_METHOD);


}
