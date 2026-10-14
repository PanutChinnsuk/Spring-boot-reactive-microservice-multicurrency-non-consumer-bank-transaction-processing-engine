package com.panut.centralbanking.appcore.servicemaps;

public class FullPath {


    public static final String loadBalancedOf(String serviceId, String methodPath) {
        return "lb://" + serviceId + InternalServiceMapping.API + methodPath;
    }

    public static final String of(String serviceId, String methodPath) {
        return "https://" + serviceId + InternalServiceMapping.API + methodPath;
    }
}
