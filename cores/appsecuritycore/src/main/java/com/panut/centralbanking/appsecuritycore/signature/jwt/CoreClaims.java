package com.panut.centralbanking.appsecuritycore.signature.jwt;

import com.panut.centralbanking.appsecuritycore.autoconfig.MyServiceSignatureConfig;
import com.panut.centralbanking.appsecuritycore.signature.secretkeys.HMACs;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import reactor.core.publisher.Mono;

import java.util.List;

public class CoreClaims {
    public static final String USER_ID_CLAIM = "userId";
    public static final String USER_AUTHORITIES_CLAIM = "userAuthorities";
    public static final String SERVICE_AUTHORITIES_CLAIM = "serviceAuthorities";




    public static Mono<Claims> toClaims(String token,
                                        MyServiceSignatureConfig config) {
       return Mono.defer(() -> {
           return Mono.just(Jwts.parser()
                   .verifyWith(HMACs.stringToSecretKey(config.getMySecretKey()))
                   .build()
                   .parseSignedClaims(token)
                   .getPayload());
       });
    }



    public static Mono<List<String>> getCollections(Claims validatedClaim,
                                             String claim) {
        return Mono.fromCallable(() -> {
            List<String> listOf = validatedClaim.get(claim, List.class);
            if (listOf == null) throwClaimNotFound(claim);

            return listOf;
        });
    }

    public static Mono<String> getValue(Claims validatedClaim,
                                 String claim) {
        return Mono.fromCallable(() -> {
            String value = validatedClaim.get(claim, String.class);
            if (value == null) throwClaimNotFound(claim);

            return value;
        });
    }


    private static void throwClaimNotFound(String claim) {
        throw new IllegalStateException("Validated claim" +
                " does not contain " + claim + " claim.");
    }
}
