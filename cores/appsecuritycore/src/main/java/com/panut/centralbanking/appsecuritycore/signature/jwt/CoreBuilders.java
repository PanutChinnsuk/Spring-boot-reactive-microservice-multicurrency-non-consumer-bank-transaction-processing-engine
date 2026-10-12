package com.panut.centralbanking.appsecuritycore.signature.jwt;

import com.panut.centralbanking.appsecuritycore.config.ServiceSignatureConfig;
import com.panut.centralbanking.appsecuritycore.signature.secretkeys.HMACs;
import io.jsonwebtoken.Jwts;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class CoreBuilders {
    private static final int safeExpireSeonds = 30;

    public static final String build(ServiceSignatureConfig config) {
        return Jwts.builder()
                .expiration(Date.from(
                        Instant.now()
                                .plus(safeExpireSeonds, ChronoUnit.SECONDS)
                                .plus(config.getRenewMinutes(), ChronoUnit.MINUTES)
                ))
                .claim(CoreClaims.SERVICE_AUTHORITIES_CLAIM, config.getServiceAuthorities())
                .signWith(HMACs
                        .stringToSecretKey(config
                                .getBetweenServicesSecretKey()))
                .compact();
    }
}
