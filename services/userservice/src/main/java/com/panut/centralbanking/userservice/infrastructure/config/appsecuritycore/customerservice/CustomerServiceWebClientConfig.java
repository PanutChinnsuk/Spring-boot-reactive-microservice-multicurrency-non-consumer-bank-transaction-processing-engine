package com.panut.centralbanking.userservice.infrastructure.config.appsecuritycore.customerservice;

import com.panut.centralbanking.appsecuritycore.servicesecurity.signaturegenerator.ServiceSignature;
import com.panut.centralbanking.appsecuritycore.signature.webclient.ServiceSignatureWebClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class CustomerServiceWebClientConfig {


    @Bean
    public WebClient customerServiceWebClient(WebClient.Builder builder,
                                              ServiceSignature customerServiceSignature) {
        return new ServiceSignatureWebClient(customerServiceSignature, builder).create();
    }


}
