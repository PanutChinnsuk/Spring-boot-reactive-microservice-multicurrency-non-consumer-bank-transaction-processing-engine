package com.panut.centralbanking.appsecuritycore.signature.webclient;

import com.panut.centralbanking.appsecuritycore.config.ServiceSignatureConfig;
import com.panut.centralbanking.appsecuritycore.servicesecurity.signaturegenerator.ServiceSignature;
import com.panut.centralbanking.appsecuritycore.signature.CoreHeaders;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

public class ServiceSignatureWebClient {
    private ServiceSignature serviceSignature;
    private WebClient.Builder webClientBuilder;

    private WebClient webClient;


    public ServiceSignatureWebClient(ServiceSignature serviceSignature,
                                     WebClient.Builder webClientBuilder) {
        this.serviceSignature = serviceSignature;
        this.webClientBuilder = webClientBuilder;
    }


    public WebClient create() {
        return webClientBuilder.clone()
                .filter((clientRequest, exchangeFunction) -> {
                    ClientRequest newRequest = ClientRequest.from(clientRequest)
                            .header(CoreHeaders.SERVICE_AUTHORIZATION_HEADER,
                                    serviceSignature.getCurrentSignature())
                            .build();
                    return exchangeFunction.exchange(newRequest);
                }).build();
    }
}
