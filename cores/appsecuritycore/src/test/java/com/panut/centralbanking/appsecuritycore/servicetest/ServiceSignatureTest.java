package com.panut.centralbanking.appsecuritycore.servicetest;

import com.panut.centralbanking.appsecuritycore.autoconfig.MyServiceSignatureConfig;
import com.panut.centralbanking.appsecuritycore.config.ServiceSignatureConfig;
import com.panut.centralbanking.appsecuritycore.servicesecurity.signaturegenerator.ServiceSignature;
import com.panut.centralbanking.appsecuritycore.signature.jwt.CoreClaims;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;

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
                    .renewMinutes(5)
                    .build());

    private MyServiceSignatureConfig myServiceSignatureConfig =
            MyServiceSignatureConfig.create(matchedSecretKey);



    @Test
    public void shouldExistClaims() throws InterruptedException {
        serviceSignature.automateSignature();
        String token = serviceSignature.getCurrentSignature();

        Claims claims = CoreClaims
                .toClaims(token, myServiceSignatureConfig).block();
        List<String> authorities = CoreClaims.getCollections(claims,
                CoreClaims.SERVICE_AUTHORITIES_CLAIM).block();
        Assertions.assertTrue(authorities.contains(authority));
        Assertions.assertTrue(authorities.size() == 1);

        Assertions.assertNotNull(serviceSignature.getCurrentSignature());
    }
}
