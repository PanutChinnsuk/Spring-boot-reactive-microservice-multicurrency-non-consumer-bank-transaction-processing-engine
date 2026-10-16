package com.panut.centralbanking.userservice.infrastructure.config;

import com.panut.centralbanking.appsecuritycore.servicesecurity.signaturegenerator.ServiceSignature;
import com.panut.centralbanking.appsecuritycore.signature.webclient.ServiceSignatureWebClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class DefaultWebClientConfig {
    @Bean
    @LoadBalanced
    public WebClient.Builder builder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient defaultWebClient(WebClient.Builder builder) {
        return builder.build();
    }




}
