package com.panut.centralbanking.userservice.infrastructure.config.appsecuritycore.customerservice;

import com.panut.centralbanking.appcore.servicemaps.map.CustomerService;
import com.panut.centralbanking.appsecuritycore.config.ServiceSignatureConfig;
import com.panut.centralbanking.appsecuritycore.core.ServiceSignatureConfigSecurityContext;
import com.panut.centralbanking.appsecuritycore.servicesecurity.signaturegenerator.ServiceSignature;
import com.panut.centralbanking.appsecuritycore.signature.webclient.ServiceSignatureWebClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class CustomerServiceConfig {
    @Value("${app.security.core.customerServiceKey}")
    private String customerServiceKey;


    @Bean
    public ServiceSignatureConfig customerServiceSignatureConfig(ServiceSignatureConfigSecurityContext context) {
        return ServiceSignatureConfig.builder()
                .optionalName("CUSTOMERSERVICE_SIGNATURE")
                .betweenServicesSecretKey(customerServiceKey)
                .renewMinutes(1)
                .ServiceSignatureConfigSecurityContext(context)
                .build();
    }




    @Bean
    public ServiceSignature customerServiceSignature(ServiceSignatureConfig customerServiceSignatureConfig) {
        return new ServiceSignature(customerServiceSignatureConfig);
    }


    @Bean
    public WebClient customerServiceWebClient(WebClient.Builder builder,
                                              ServiceSignature customerServiceSignature) {
        return new ServiceSignatureWebClient(customerServiceSignature, builder).create();
    }










}
