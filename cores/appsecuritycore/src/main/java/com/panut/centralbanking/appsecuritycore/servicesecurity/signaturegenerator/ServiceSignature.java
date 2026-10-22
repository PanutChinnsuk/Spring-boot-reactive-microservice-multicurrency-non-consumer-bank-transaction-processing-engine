package com.panut.centralbanking.appsecuritycore.servicesecurity.signaturegenerator;

import com.panut.centralbanking.appsecuritycore.autoconfig.TaskSchedulerAutoConfiguration;
import com.panut.centralbanking.appsecuritycore.config.ServiceSignatureConfig;
import com.panut.centralbanking.appsecuritycore.signature.jwt.CoreBuilders;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class ServiceSignature {
    private final ServiceSignatureConfig serviceSignatureConfig;

    @Autowired
    private TaskScheduler taskScheduler;



    public ServiceSignature(ServiceSignatureConfig serviceSignatureConfig) {
        this.serviceSignatureConfig = serviceSignatureConfig;



    }


    private AtomicReference<String> currentSignature = new AtomicReference<>();
    private AtomicInteger version = new AtomicInteger(0);

    @PostConstruct
    public void schedule() {
        taskScheduler.scheduleWithFixedDelay(
                this::automateSignature,
                Duration.ofMinutes(serviceSignatureConfig.getRenewMinutes())
        );
    }


    public void automateSignature() {
        String configName = serviceSignatureConfig.getOptionalName();
        log.info("Before version increment: {}", version.get());
        log.info("Before currentSignature renew: {}", configName);
        this.currentSignature.set(CoreBuilders
                .build(serviceSignatureConfig));
        int incremented = version.incrementAndGet();
        log.info("currentSignature not null: {}", Boolean.toString(
                currentSignature.get() != null));
        log.info("Renewed currentSignature: {}", configName);
        log.info("Incremented version: {}", incremented);

        log.debug("ServiceSignature signing with {}",
                serviceSignatureConfig.getBetweenServicesSecretKey());

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
