package com.panut.centralbanking.transactionservice.infrastructure.config.appsecuritycore.internalaccountservice;



import com.panut.centralbanking.appcore.servicemaps.map.InternalAccountsService;
import com.panut.centralbanking.appsecuritycore.config.ServiceSignatureConfig;
import com.panut.centralbanking.appsecuritycore.core.ServiceSignatureConfigSecurityContext;
import com.panut.centralbanking.appsecuritycore.servicesecurity.signaturegenerator.ServiceSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InternalAccountServiceConfig {
    @Value("${app.security.core.accountServiceKey}")
    private String accountServiceKey;



    @Bean
    public ServiceSignatureConfig internalAccountServiceSignatureConfig(
            ServiceSignatureConfigSecurityContext context
    ) {
        return ServiceSignatureConfig.builder()
                .ServiceSignatureConfigSecurityContext(context)
                .serviceAuthorities(InternalAccountsService.GET_FX_INTERNAL_ACCOUNTS_METHOD)
                .optionalName("INTERNAL ACCOUNT SERVICE SIGNATURE")
                .renewMinutes(1)
                .betweenServicesSecretKey(accountServiceKey)
                .build();
    }

    @Bean
    public ServiceSignature internalAccountServiceSignature(ServiceSignatureConfig internalAccountServiceSignatureConfig) {
        return new ServiceSignature(internalAccountServiceSignatureConfig);
    }


}
