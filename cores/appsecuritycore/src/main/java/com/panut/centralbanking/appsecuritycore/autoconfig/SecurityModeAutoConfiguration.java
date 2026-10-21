package com.panut.centralbanking.appsecuritycore.autoconfig;

import com.panut.centralbanking.appsecuritycore.core.SecurityMode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@AutoConfiguration
@ConfigurationProperties("app.security.core")
@Slf4j
public class SecurityModeAutoConfiguration {
    private SecurityMode securityMode = SecurityMode.DEFAULT;



    public void setSecurityMode(SecurityMode securityMode) {

        this.securityMode = securityMode;
        log.info("SecurityModeAutoConfiguration mode, {}", securityMode);
    }


    public SecurityMode getSecurityMode() {
        return securityMode;
    }



}
