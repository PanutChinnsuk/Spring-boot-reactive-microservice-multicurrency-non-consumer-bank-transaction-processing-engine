package com.panut.centralbanking.transactionservice.infrastructure.config.appsecuritycore.accountservice;


import com.panut.centralbanking.appcore.servicemaps.map.AccountService;
import com.panut.centralbanking.appsecuritycore.config.ServiceSignatureConfig;
import com.panut.centralbanking.appsecuritycore.core.ServiceSignatureConfigSecurityContext;
import com.panut.centralbanking.appsecuritycore.servicesecurity.signaturegenerator.ServiceSignature;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountServiceConfig {
    @Value("${app.security.core.accountServiceKey}")
    private String accountServiceKey;

    @Bean
    public ServiceSignatureConfig accountServiceSignatureConfig(
            ServiceSignatureConfigSecurityContext context
    ) {
        return ServiceSignatureConfig.builder()
                .ServiceSignatureConfigSecurityContext(context)
                .serviceAuthorities(AccountService.ACCOUNTS_STATE_VERIFY_METHOD)
                .optionalName("ACCOUNT SERVICE SIGNATURE")
                .renewMinutes(1)
                .betweenServicesSecretKey(accountServiceKey)
                .build();
    }

    @Bean
    public ServiceSignature accountServiceSignature(ServiceSignatureConfig accountServiceSignatureConfig) {
        return new ServiceSignature(accountServiceSignatureConfig);
    }


}
