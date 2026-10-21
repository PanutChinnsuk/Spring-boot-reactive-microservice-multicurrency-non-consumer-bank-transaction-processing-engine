package com.panut.centralbanking.appsecuritycore.servicetest;

import com.panut.centralbanking.appsecuritycore.autoconfig.MyServiceSignatureAutoConfiguration;
import com.panut.centralbanking.appsecuritycore.autoconfig.SecurityModeAutoConfiguration;
import com.panut.centralbanking.appsecuritycore.config.ServiceSignatureConfig;
import com.panut.centralbanking.appsecuritycore.core.ServiceSignatureConfigSecurityContext;
import com.panut.centralbanking.appsecuritycore.servicesecurity.signaturegenerator.ServiceSignature;
import com.panut.centralbanking.appsecuritycore.signature.jwt.CoreClaims;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.List;


public class ServiceSignatureTest {
    private String matchedSecretKey = "secretKeysecretKeysecretKeysecretKey" +
            "secretKeysecretKeysecretKeysecretKey";

    private String authority = "Auth1";
    private ServiceSignature serviceSignature =
            new ServiceSignature(ServiceSignatureConfig
                    .builder()
                    .serviceAuthorities(authority)
                    .optionalName("configName")
                    .betweenServicesSecretKey(matchedSecretKey)
                    .ServiceSignatureConfigSecurityContext(
                            ServiceSignatureConfigSecurityContext.DEFAULT()
                    )
                    .renewMinutes(5)
                    .build());

    private MyServiceSignatureAutoConfiguration myServiceSignatureAutoConfiguration =
            MyServiceSignatureAutoConfiguration.create(matchedSecretKey,
                    ServiceSignatureConfigSecurityContext.DEFAULT());



    @Test
    public void shouldExistClaims() throws InterruptedException {
        serviceSignature.automateSignature();
        String token = serviceSignature.getCurrentSignature();

        Claims claims = CoreClaims
                .toClaims(token, myServiceSignatureAutoConfiguration.getMySecretKeyHMAC()).block();
        List<String> authorities = CoreClaims.getCollectionMono(claims,
                CoreClaims.SERVICE_AUTHORITIES_CLAIM).block();
        Assertions.assertTrue(authorities.contains(authority));
        Assertions.assertTrue(authorities.size() == 1);

        Assertions.assertNotNull(serviceSignature.getCurrentSignature());
    }
}
