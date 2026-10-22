package com.panut.centralbanking.transactionservice.infrastructure.config.appsecuritycore.accountservice;

import com.panut.centralbanking.appsecuritycore.servicesecurity.signaturegenerator.ServiceSignature;
import com.panut.centralbanking.appsecuritycore.signature.webclient.ServiceSignatureWebClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AccountServiceWebClientConfig {


    @Bean
    public WebClient accountServiceWebClient(ServiceSignature accountServiceSignatureConfig,
                                             WebClient.Builder builder) {
        return new ServiceSignatureWebClient(accountServiceSignatureConfig, builder).create();
    }
}
