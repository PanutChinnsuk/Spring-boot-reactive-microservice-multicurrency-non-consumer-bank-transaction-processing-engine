package com.panut.centralbanking.appsecuritycore.autoconfig;


import com.panut.centralbanking.appsecuritycore.signature.secretkeys.HMACs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.crypto.SecretKey;

@AutoConfiguration
@ConfigurationProperties(prefix = "app.security.core")
@Slf4j
public class MyServiceSignatureAutoConfiguration {
    private String mySecretKey;

    public static MyServiceSignatureAutoConfiguration create(String any) {
        MyServiceSignatureAutoConfiguration config = new MyServiceSignatureAutoConfiguration();
        config.setMySecretKey(any);

        return config;
    }


    public void setMySecretKey(String mySecretKey) {
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
