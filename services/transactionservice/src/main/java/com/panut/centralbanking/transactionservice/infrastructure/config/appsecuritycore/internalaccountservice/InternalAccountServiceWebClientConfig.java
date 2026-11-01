package com.panut.centralbanking.transactionservice.infrastructure.config.appsecuritycore.internalaccountservice;

import com.panut.centralbanking.appsecuritycore.servicesecurity.signaturegenerator.ServiceSignature;
import com.panut.centralbanking.appsecuritycore.signature.webclient.ServiceSignatureWebClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class InternalAccountServiceWebClientConfig {


    @Bean
    public WebClient internalAccountServiceWebClient(ServiceSignature internalAccountServiceSignature,
                                             WebClient.Builder builder) {
        return new ServiceSignatureWebClient(internalAccountServiceSignature, builder).create();
    }
}
