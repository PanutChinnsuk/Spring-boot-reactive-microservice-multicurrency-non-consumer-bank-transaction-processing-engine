package com.panut.centralbanking.appsecuritycore.autoconfig;

import com.panut.centralbanking.appsecuritycore.core.ServiceSignatureConfigSecurityContext;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class ServiceSignatureConfigSecurityContextAutoConfiguration {
    private final SecurityModeAutoConfiguration securityModeAutoConfiguration;

    public ServiceSignatureConfigSecurityContextAutoConfiguration(SecurityModeAutoConfiguration securityModeAutoConfiguration) {
        this.securityModeAutoConfiguration = securityModeAutoConfiguration;
    }


    @Bean
    public ServiceSignatureConfigSecurityContext serviceSignatureConfigSecurityContext() {
        return new ServiceSignatureConfigSecurityContext(
                securityModeAutoConfiguration.getSecurityMode()
        );
    }
}
