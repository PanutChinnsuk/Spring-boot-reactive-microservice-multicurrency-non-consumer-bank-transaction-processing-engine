package com.panut.centralbanking.appsecuritycore.autoconfig;


import com.panut.centralbanking.appsecuritycore.signature.secretkeys.HMACs;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@ConfigurationProperties(prefix = "app.security.core")
public class MyServiceSignatureConfig {
    private String mySecretKey;

    public static MyServiceSignatureConfig create(String any) {
        MyServiceSignatureConfig config = new MyServiceSignatureConfig();
        config.setMySecretKey(any);

        return config;
    }


    public void setMySecretKey(String mySecretKey) {
        this.mySecretKey = mySecretKey;
    }

    public String getMySecretKey() {
        return mySecretKey;
    }
}
