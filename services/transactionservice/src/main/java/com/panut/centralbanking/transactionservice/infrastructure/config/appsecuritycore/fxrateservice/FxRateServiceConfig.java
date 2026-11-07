package com.panut.centralbanking.transactionservice.infrastructure.config.appsecuritycore.fxrateservice;

import com.panut.centralbanking.appcore.servicemaps.map.FxRateService;
import com.panut.centralbanking.appsecuritycore.config.ServiceSignatureConfig;
import com.panut.centralbanking.appsecuritycore.core.ServiceSignatureConfigSecurityContext;
import com.panut.centralbanking.appsecuritycore.servicesecurity.signaturegenerator.ServiceSignature;
import com.panut.centralbanking.appsecuritycore.signature.webclient.ServiceSignatureWebClient;
import com.panut.centralbanking.transactionservice.infrastructure.config.appsecuritycore.AppSecurityCoreProperty;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class FxRateServiceConfig {
    private final AppSecurityCoreProperty property;

    public FxRateServiceConfig(AppSecurityCoreProperty property) {
        this.property = property;
    }

    @Bean
    public ServiceSignatureConfig fxRateSignatureConfig(ServiceSignatureConfigSecurityContext context) {
        return ServiceSignatureConfig.builder()
                .renewMinutes(1)
                .betweenServicesSecretKey(property.getFxRateServiceKey())
                .optionalName("FX RATE SERVICE SIGNATURE")
                .serviceAuthorities(FxRateService.GET_LATEST_METHOD)
                .ServiceSignatureConfigSecurityContext(context)
                .build();
    }

    @Bean
    public ServiceSignature fxRateServiceSignature(ServiceSignatureConfig fxRateSignatureConfig) {
        return new ServiceSignature(fxRateSignatureConfig);
    }

    @Bean
    public WebClient fxRateServiceWebClient(ServiceSignature fxRateServiceSignature,
                                            WebClient.Builder builder) {
        return new ServiceSignatureWebClient(fxRateServiceSignature, builder)
                .create();
    }
}
