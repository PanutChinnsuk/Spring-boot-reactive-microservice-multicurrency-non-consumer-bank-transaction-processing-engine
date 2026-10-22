package com.panut.centralbanking.appsecuritycore.autoconfig;

import com.panut.centralbanking.appsecuritycore.core.SecurityMode;
import com.panut.centralbanking.appsecuritycore.core.ServiceSignatureConfigSecurityContext;
import com.panut.centralbanking.appsecuritycore.core.TestMode;
import com.panut.centralbanking.appsecuritycore.signature.secretkeys.HMACs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.crypto.SecretKey;

@AutoConfiguration
@ConfigurationProperties("app.security.core")
@Slf4j
public class PrimarySignatureSecretKeyAutoConfiguration {
    private final ServiceSignatureConfigSecurityContext serviceSignatureConfigSecurityContext;


    public PrimarySignatureSecretKeyAutoConfiguration(ServiceSignatureConfigSecurityContext serviceSignatureConfigSecurityContext) {
        this.serviceSignatureConfigSecurityContext = serviceSignatureConfigSecurityContext;
    }

    private String apiKey;


    public void setApiKey(String apiKey) {
        if (serviceSignatureConfigSecurityContext.getSecurityMode().equals(SecurityMode.TEST)) {
            this.apiKey = TestMode.toSalt(apiKey);
            log.info("PrimarySignatureSecretKeyAutoConfiguration apiKey " +
                    "auto configuration as test mode.");
            return;
        }



        this.apiKey = apiKey;

        log.info("PrimarySignatureSecretKeyAutoConfiguration apiKey auto configuration.");
    }



    public SecretKey getApiKeyHMAC() {
        if (apiKey == null) throw new IllegalStateException("PrimarySignatureSecretKeyAutoConfiguration" +
                " apiKey has not yet been initialized.");

        return HMACs.stringToSecretKey(apiKey);
    }
}
