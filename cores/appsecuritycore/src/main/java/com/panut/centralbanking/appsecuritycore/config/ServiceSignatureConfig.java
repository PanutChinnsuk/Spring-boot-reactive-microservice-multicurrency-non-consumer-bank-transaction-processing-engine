package com.panut.centralbanking.appsecuritycore.config;

import com.panut.centralbanking.appsecuritycore.autoconfig.SecurityModeAutoConfiguration;
import com.panut.centralbanking.appsecuritycore.core.SecurityMode;
import com.panut.centralbanking.appsecuritycore.core.ServiceSignatureConfigSecurityContext;
import com.panut.centralbanking.appsecuritycore.core.TestMode;
import com.panut.centralbanking.appsecuritycore.signature.secretkeys.HMACs;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@Slf4j
public class ServiceSignatureConfig {
    private final List<String> serviceAuthorities;
    private int renewMinutes = 15;
    private final String betweenServicesSecretKey;
    private String optionalName = "UNDEFINED ServiceSignatureConfig.optionalName";
    private final ServiceSignatureConfigSecurityContext context;


    public ServiceSignatureConfig(List<String> serviceAuthorities,
                                  int renewMinutes,
                                  String betweenServicesSecretKey,
                                  String optionalName,
                                  ServiceSignatureConfigSecurityContext context) {
        this.serviceAuthorities = serviceAuthorities;
        this.renewMinutes = renewMinutes;
        this.betweenServicesSecretKey = betweenServicesSecretKey;
        this.optionalName = optionalName;
        this.context = context;

        log.info(optionalName + " security mode: " + context.getSecurityMode());
    }


    public static Builder builder() {
        return new Builder();
    }



    public static class Builder {
        private List<String> serviceAuthorities;
        private int renewMinutes = 1;
        private String betweenServicesSecretKey;
        private String optionalName;
        private ServiceSignatureConfigSecurityContext context;

        public Builder serviceAuthorities(String... authorities) {
            this.serviceAuthorities = Arrays.stream(authorities).toList();
            return this;
        }

        public Builder renewMinutes(int renewMinutes) {
            this.renewMinutes = renewMinutes;
            return this;
        }

        public Builder betweenServicesSecretKey(String betweenServicesSecretKey) {
            this.betweenServicesSecretKey = betweenServicesSecretKey;
            return this;
        }

        public Builder optionalName(String optionalName) {
            this.optionalName = optionalName;
            return this;
        }

        public Builder ServiceSignatureConfigSecurityContext
                (ServiceSignatureConfigSecurityContext context) {
            this.context = context;
            return this;
        }

        public ServiceSignatureConfig build() {
            if (serviceAuthorities == null) serviceAuthorities = Collections
                    .emptyList();



            return new ServiceSignatureConfig(serviceAuthorities, renewMinutes,
                    betweenServicesSecretKey, optionalName, context);
        }
    }

    public int getRenewMinutes() {
        return renewMinutes;
    }

    public List<String> getServiceAuthorities() {
        return serviceAuthorities;
    }

    public String getOptionalName() {
        return optionalName;
    }

    public String getBetweenServicesSecretKey() {
        if (context.getSecurityMode().equals(SecurityMode.TEST))
            return TestMode.toSalt(betweenServicesSecretKey);

        return betweenServicesSecretKey;
    }

    public SecretKey getBetweenServicesSecretKeyHMAC() {
        return HMACs.stringToSecretKey(getBetweenServicesSecretKey());
    }
}
