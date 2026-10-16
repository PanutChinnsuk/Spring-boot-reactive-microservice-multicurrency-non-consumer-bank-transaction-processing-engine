package com.panut.centralbanking.appsecuritycore.config;

import com.panut.centralbanking.appsecuritycore.servicesecurity.signaturegenerator.ServiceSignature;
import com.panut.centralbanking.appsecuritycore.signature.secretkeys.HMACs;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class ServiceSignatureConfig {
    private List<String> serviceAuthorities;
    private int renewMinutes;
    private String betweenServicesSecretKey;
    private String optionalName = "UNDEFINED ServiceSignatureConfig.optionalName";


    public ServiceSignatureConfig(List<String> serviceAuthorities,
                                  int renewMinutes,
                                  String betweenServicesSecretKey,
                                  String optionalName) {
        this.serviceAuthorities = serviceAuthorities;
        this.renewMinutes = renewMinutes;
        this.betweenServicesSecretKey = betweenServicesSecretKey;
        this.optionalName = optionalName;
    }


    public static Builder builder() {
        return new Builder();
    }



    public static class Builder {
        private List<String> serviceAuthorities;
        private int renewMinutes = 1;
        private String betweenServicesSecretKey;
        private String optionalName;

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

        public ServiceSignatureConfig build() {
            return new ServiceSignatureConfig(serviceAuthorities, renewMinutes,
                    betweenServicesSecretKey, optionalName);
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
        return betweenServicesSecretKey;
    }

    public SecretKey getBetweenServicesSecretKeyHMAC() {
        return HMACs.stringToSecretKey(betweenServicesSecretKey);
    }
}
