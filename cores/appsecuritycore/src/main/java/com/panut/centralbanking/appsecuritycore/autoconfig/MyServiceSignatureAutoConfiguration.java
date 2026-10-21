package com.panut.centralbanking.appsecuritycore.autoconfig;


import com.panut.centralbanking.appsecuritycore.core.SecurityMode;
import com.panut.centralbanking.appsecuritycore.core.ServiceSignatureConfigSecurityContext;
import com.panut.centralbanking.appsecuritycore.signature.secretkeys.HMACs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.crypto.SecretKey;

@AutoConfiguration
@ConfigurationProperties(prefix = "app.security.core")
@Slf4j
public class MyServiceSignatureAutoConfiguration {
    private final ServiceSignatureConfigSecurityContext serviceSignatureConfigSecurityContext;

    public MyServiceSignatureAutoConfiguration(ServiceSignatureConfigSecurityContext serviceSignatureConfigSecurityContext) {
        this.serviceSignatureConfigSecurityContext = serviceSignatureConfigSecurityContext;
    }

    private String mySecretKey;

    public static MyServiceSignatureAutoConfiguration create(String any,
                                                             ServiceSignatureConfigSecurityContext context) {
        MyServiceSignatureAutoConfiguration config = new MyServiceSignatureAutoConfiguration(context);
        config.setMySecretKey(any);

        return config;
    }


    public void setMySecretKey(String mySecretKey) {
        if (serviceSignatureConfigSecurityContext.getSecurityMode().equals(
                SecurityMode.TEST
        )) {
            this.mySecretKey = mySecretKey + "saltSaltsaltSaltsaltSaltsaltSaltsaltSaltsaltSaltsaltSaltsaltSaltsaltSaltsaltSaltsaltSaltsaltSaltsaltSaltsaltSalt";
            log.info("MyServiceSignatureAutoConfiguration mySecretKey auto configuration" +
                    " as test mode.");
            return;
        }


        this.mySecretKey = mySecretKey;

        log.info("MyServiceSignatureAutoConfiguration mySecretKey auto configuration.");
    }

    public String getMySecretKey() {
        return mySecretKey;
    }

    public SecretKey getMySecretKeyHMAC() {
        return HMACs.stringToSecretKey(mySecretKey);
    }
}
