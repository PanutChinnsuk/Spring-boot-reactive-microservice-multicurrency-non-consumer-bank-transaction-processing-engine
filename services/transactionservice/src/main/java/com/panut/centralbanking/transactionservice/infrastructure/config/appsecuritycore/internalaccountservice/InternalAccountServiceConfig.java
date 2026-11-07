package com.panut.centralbanking.transactionservice.infrastructure.config.appsecuritycore.internalaccountservice;



import com.panut.centralbanking.appcore.servicemaps.map.InternalAccountsService;
import com.panut.centralbanking.appsecuritycore.config.ServiceSignatureConfig;
import com.panut.centralbanking.appsecuritycore.core.ServiceSignatureConfigSecurityContext;
import com.panut.centralbanking.appsecuritycore.servicesecurity.signaturegenerator.ServiceSignature;
import com.panut.centralbanking.appsecuritycore.signature.webclient.ServiceSignatureWebClient;
import com.panut.centralbanking.transactionservice.infrastructure.config.appsecuritycore.AppSecurityCoreProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class InternalAccountServiceConfig {
    private final AppSecurityCoreProperty property;

    public InternalAccountServiceConfig(AppSecurityCoreProperty property) {
        this.property = property;
    }



    @Bean
    public ServiceSignatureConfig internalAccountServiceSignatureConfig(
            ServiceSignatureConfigSecurityContext context
    ) {
        return ServiceSignatureConfig.builder()
                .ServiceSignatureConfigSecurityContext(context)
                .serviceAuthorities(InternalAccountsService.GET_FX_INTERNAL_ACCOUNTS_METHOD)
                .optionalName("INTERNAL ACCOUNT SERVICE SIGNATURE")
                .renewMinutes(1)
                .betweenServicesSecretKey(property.getAccountServiceKey())
                .build();
    }

    @Bean
    public ServiceSignature internalAccountServiceSignature(ServiceSignatureConfig internalAccountServiceSignatureConfig) {
        return new ServiceSignature(internalAccountServiceSignatureConfig);
    }

    @Bean
    public WebClient internalAccountServiceWebClient(ServiceSignature internalAccountServiceSignature,
                                                     WebClient.Builder builder) {
        return new ServiceSignatureWebClient(internalAccountServiceSignature, builder).create();
    }

}
