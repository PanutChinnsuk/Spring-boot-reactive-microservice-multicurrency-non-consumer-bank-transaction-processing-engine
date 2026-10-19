package com.panut.centralbanking.userservice.infrastructure.config.appsecuritycore.customerservice;

import com.panut.centralbanking.appcore.servicemaps.map.CustomerService;
import com.panut.centralbanking.appsecuritycore.config.ServiceSignatureConfig;
import com.panut.centralbanking.appsecuritycore.servicesecurity.signaturegenerator.ServiceSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class CustomerServiceConfig {
    @Value("${app.security.core.customerServiceKey}")
    private String customerServiceKey;


    @Bean
    public ServiceSignatureConfig customerServiceSignatureConfig() {
        return ServiceSignatureConfig.builder()
                .optionalName("CUSTOMERSERVICE_SIGNATURE")
                .betweenServicesSecretKey(customerServiceKey)
                .renewMinutes(1)
                .build();
    }




    @Bean
    public ServiceSignature customerServiceSignature(ServiceSignatureConfig customerServiceSignatureConfig) {
        return new ServiceSignature(customerServiceSignatureConfig);
    }











}
