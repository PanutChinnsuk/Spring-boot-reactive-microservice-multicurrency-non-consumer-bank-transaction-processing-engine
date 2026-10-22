package com.panut.centralbanking.appsecuritycore.core;


public class ServiceSignatureConfigSecurityContext {
    private SecurityMode securityMode;



    public SecurityMode getSecurityMode() {
        return securityMode;
    }

    public ServiceSignatureConfigSecurityContext(SecurityMode securityMode) {
        this.securityMode = securityMode;
    }


    public static ServiceSignatureConfigSecurityContext DEFAULT() {
        return new ServiceSignatureConfigSecurityContext(SecurityMode.DEFAULT);
    }
}
