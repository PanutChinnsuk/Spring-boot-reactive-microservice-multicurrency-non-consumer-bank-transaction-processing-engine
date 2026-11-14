package com.panut.centralbanking.transactionservice.infrastructure.config.appsecuritycore.accountservice;


import com.panut.centralbanking.appcore.servicemaps.map.AccountService;
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
public class AccountServiceConfig {
    private final AppSecurityCoreProperty property;

    public AccountServiceConfig(AppSecurityCoreProperty property) {
        this.property = property;
    }

    @Bean
    public ServiceSignatureConfig accountServiceSignatureConfig(
            ServiceSignatureConfigSecurityContext context
    ) {
        return ServiceSignatureConfig.builder()
                .ServiceSignatureConfigSecurityContext(context)
                .serviceAuthorities(AccountService.ACCOUNTS_STATE_VERIFY_METHOD,
                        AccountService.ACCOUNTS_INFO_FOR_TRANSACTION_METHOD,
                        AccountService.GET_PROCESSED_BALANCE_CHANGE_EVENT_METHOD)
                .optionalName("ACCOUNT SERVICE SIGNATURE")
                .renewMinutes(1)
                .betweenServicesSecretKey(property.getAccountServiceKey())
                .build();
    }

    @Bean
    public ServiceSignature accountServiceSignature(ServiceSignatureConfig accountServiceSignatureConfig) {
        return new ServiceSignature(accountServiceSignatureConfig);
    }


    @Bean
    public WebClient accountServiceWebClient(ServiceSignature accountServiceSignature,
                                             WebClient.Builder builder) {
        return new ServiceSignatureWebClient(accountServiceSignature, builder).create();
    }

}
