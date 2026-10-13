package com.panut.centralbanking.appsecuritycore.autoconfig;

import com.panut.centralbanking.appsecuritycore.signature.secretkeys.HMACs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.crypto.SecretKey;

@AutoConfiguration
@ConfigurationProperties("app.security.core")
@Slf4j
public class PrimarySignatureSecretKeyAutoConfiguration {
    private String apiKey;


    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
        log.info("PrimarySignatureSecretKeyAutoConfiguration apiKey auto config.");
    }

    public String getApiKey() {
        return apiKey;
    }

    public SecretKey getApiKeyHMAC() {
        if (apiKey == null) throw new IllegalStateException("PrimarySignatureSecretKeyAutoConfiguration" +
                " apiKey has not yet been initialized.");

        return HMACs.stringToSecretKey(apiKey);
    }
}
