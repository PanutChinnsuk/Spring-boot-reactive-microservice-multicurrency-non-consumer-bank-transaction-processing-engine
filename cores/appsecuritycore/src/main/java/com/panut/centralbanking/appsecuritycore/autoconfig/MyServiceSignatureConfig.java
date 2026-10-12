package com.panut.centralbanking.appsecuritycore.autoconfig;


import com.panut.centralbanking.appsecuritycore.signature.secretkeys.HMACs;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@AutoConfiguration
@ConfigurationProperties(prefix = "app.security.core")
@Slf4j
public class MyServiceSignatureConfig {
    private String mySecretKey;

    public static MyServiceSignatureConfig create(String any) {
        MyServiceSignatureConfig config = new MyServiceSignatureConfig();
        config.setMySecretKey(any);

        return config;
    }


    public void setMySecretKey(String mySecretKey) {
        this.mySecretKey = mySecretKey;
        log.info("MyServiceSignatureConfig auto configuration.");
    }

    public String getMySecretKey() {
        return mySecretKey;
    }
}
