package com.panut.centralbanking.accountservice.infrastructure.config.appsecuritycore.transactionservice;

import com.panut.centralbanking.accountservice.infrastructure.config.appsecuritycore.AppSecurityCoreProperty;
import com.panut.centralbanking.appcore.servicemaps.map.TransactionService;
import com.panut.centralbanking.appsecuritycore.config.ServiceSignatureConfig;
import com.panut.centralbanking.appsecuritycore.core.ServiceSignatureConfigSecurityContext;
import com.panut.centralbanking.appsecuritycore.servicesecurity.signaturegenerator.ServiceSignature;
import com.panut.centralbanking.appsecuritycore.signature.webclient.ServiceSignatureWebClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class TransactionServiceConfig {
    private final AppSecurityCoreProperty property;

    public TransactionServiceConfig(AppSecurityCoreProperty property) {
        this.property = property;
    }

    @Bean
    public ServiceSignatureConfig transactionServiceSignatureConfig(
            ServiceSignatureConfigSecurityContext context
    ) {
        return ServiceSignatureConfig.builder()
                .serviceAuthorities(TransactionService.TRANSACTION_CLEAR_PENDING_BALANCE_METHOD)
                .ServiceSignatureConfigSecurityContext(context)
                .renewMinutes(1)
                .optionalName("TRANSACTION SERVICE SIGNATURE")
                .betweenServicesSecretKey(property.getTransactionServiceKey())
                .build();
    }


    @Bean
    public ServiceSignature transactionServiceSignature(
            ServiceSignatureConfig transactionServiceSignatureConfig) {
        return new ServiceSignature(transactionServiceSignatureConfig);
    }

    @Bean
    public WebClient transactionServiceWebClient(
            ServiceSignature transactionServiceSignature,
            WebClient.Builder builder
    ) {
        return new ServiceSignatureWebClient(transactionServiceSignature, builder)
                .create();
    }
}
