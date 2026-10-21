package com.panut.centralbanking.transactionservice.infrastructure.config.appsecuritycore.fxrateservice;

import com.panut.centralbanking.appcore.servicemaps.map.FxRateService;
import com.panut.centralbanking.appsecuritycore.config.ServiceSignatureConfig;
import com.panut.centralbanking.appsecuritycore.core.ServiceSignatureConfigSecurityContext;
import com.panut.centralbanking.appsecuritycore.servicesecurity.signaturegenerator.ServiceSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FxRateServiceConfig {
    @Value("${app.security.core.fxRateServiceKey}")
    private String fxRateServiceKey;

    @Bean
    public ServiceSignatureConfig fxRateSignatureConfig(ServiceSignatureConfigSecurityContext context) {
        return ServiceSignatureConfig.builder()
                .renewMinutes(1)
                .betweenServicesSecretKey(fxRateServiceKey)
                .optionalName("FX RATE SERVICE SIGNATURE")
                .serviceAuthorities(FxRateService.GET_LATEST_METHOD)
                .ServiceSignatureConfigSecurityContext(context)
                .build();
    }

    @Bean
    public ServiceSignature fxRateServiceSignature(ServiceSignatureConfig fxRateSignatureConfig) {
        return new ServiceSignature(fxRateSignatureConfig);
    }
}
