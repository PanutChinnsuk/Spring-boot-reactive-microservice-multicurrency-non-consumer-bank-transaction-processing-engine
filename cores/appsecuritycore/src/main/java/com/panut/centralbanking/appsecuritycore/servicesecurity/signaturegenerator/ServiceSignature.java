package com.panut.centralbanking.appsecuritycore.servicesecurity.signaturegenerator;

import com.panut.centralbanking.appsecuritycore.config.ServiceSignatureConfig;
import com.panut.centralbanking.appsecuritycore.signature.jwt.CoreBuilders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class ServiceSignature {
    private final ServiceSignatureConfig serviceSignatureConfig;

    public ServiceSignature(ServiceSignatureConfig serviceSignatureConfig) {
        this.serviceSignatureConfig = serviceSignatureConfig;
    }


    private AtomicReference<String> currentSignature = new AtomicReference<>();
    private AtomicInteger version = new AtomicInteger(0);


    @Scheduled(fixedDelayString = "#{1000 * 60 * @serviceSignatureConfig.renewMinutes}")
    public void automateSignature() {
        String configName = serviceSignatureConfig.getOptionalName();
        log.info("Before version increment: {}", version.get());
        log.info("Before currentSignature renew: {}", configName);
        this.currentSignature.set(CoreBuilders
                .build(serviceSignatureConfig));
        int incremented = version.incrementAndGet();
        log.info("Renewed currentSignature: {}", configName);
        log.info("Incremented version: {}", incremented);
    }


    public String getCurrentSignature() {
        String value = currentSignature.get();
        if (value == null) throw new IllegalStateException(
                "Current signature has not yet been initialized."
        );
        return value;
    }

    public int getCurrentVersion() {
        return version.get();
    }
}
