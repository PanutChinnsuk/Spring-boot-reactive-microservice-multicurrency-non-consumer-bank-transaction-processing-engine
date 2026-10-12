package com.panut.centralbanking.appsecuritycore.servicesecurity.context;

import java.util.Collections;
import java.util.List;

public class ServiceSecurityContext {
    private List<String> authorities;


    public ServiceSecurityContext(List<String> authorities) {
        this.authorities = authorities;
    }

    public static ServiceSecurityContext emptyContext() {
        return new ServiceSecurityContext(Collections.emptyList());
    }


    public List<String> getAuthorities() {
        return authorities;
    }

    public boolean contain(String authority) {
        return authorities.contains(authority);
    }
}
