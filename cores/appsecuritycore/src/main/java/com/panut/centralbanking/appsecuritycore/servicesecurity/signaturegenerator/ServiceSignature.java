package com.panut.centralbanking.appsecuritycore.servicesecurity.signaturegenerator;

import com.panut.centralbanking.appsecuritycore.config.ServiceSignatureConfig;
import com.panut.centralbanking.appsecuritycore.signature.jwt.CoreBuilders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
public class ServiceSignature {
    private final ServiceSignatureConfig serviceSignatureConfig;

    public ServiceSignature(ServiceSignatureConfig serviceSignatureConfig) {
        this.serviceSignatureConfig = serviceSignatureConfig;
    }


    private String currentSignature;


    @Scheduled(fixedDelayString = "#{1000 * 60 * @serviceSignatureConfig.renewMinutes}")
    public void automateSignature() {
        String configName = serviceSignatureConfig.getOptionalName();
        log.info("Before currentSignature renew: {}", configName);
        this.currentSignature = CoreBuilders
                .build(serviceSignatureConfig);
        log.info("Renewed currentSignature: {}", configName);
    }


    public String getCurrentSignature() {
        if (currentSignature == null) throw new IllegalStateException(
                "Current signature has not yet been initialized."
        );
        return currentSignature;
    }
}
