package com.panut.centralbanking.appsecuritycore.signature.jwt;

import com.panut.centralbanking.appcore.exceptions.Unauthorized;
import com.panut.centralbanking.appsecuritycore.signature.secretkeys.HMACs;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.crypto.SecretKey;
import java.util.List;

@Slf4j
public class CoreClaims {
    public static final String USER_ID_CLAIM = "userId";
    public static final String USER_AUTHORITIES_CLAIM = "userAuthorities";
    public static final String USER_NAME_CLAIM = "username";

    public static final String SERVICE_AUTHORITIES_CLAIM = "serviceAuthorities";




    public static Mono<Claims> toClaims(String token,
                                        SecretKey secretKey) {
       return Mono.defer(() -> {
           log.debug("CoreClaims toClaims using {}", token);

           return Mono.just(Jwts.parser()
                   .verifyWith(secretKey)
                   .build()
                   .parseSignedClaims(token)
                   .getPayload())
                   .onErrorMap(error -> new Unauthorized(
                           error.getLocalizedMessage()
                   ));
       }).subscribeOn(Schedulers.boundedElastic());
    }



    public static List<String> getCollection(Claims validatedClaim,
                                      String claim) {
        List<String> listOf = validatedClaim.get(claim, List.class);
        if (listOf == null) throwClaimNotFound(claim);

        return listOf;
    }

    public static String getValue(Claims validatedClaim,
                                  String claim) {
        String value = validatedClaim.get(claim, String.class);
        if (value == null) throwClaimNotFound(claim);

        return value;
    }

    public static Mono<List<String>> getCollectionMono(Claims validatedClaim,
                                                       String claim) {
        return Mono.fromCallable(() -> getCollection(validatedClaim, claim))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public static Mono<String> getValueMono(Claims validatedClaim,
                                            String claim) {
        return Mono.fromCallable(() -> getValue(validatedClaim, claim))
                .subscribeOn(Schedulers.boundedElastic());
    }


    private static void throwClaimNotFound(String claim) {
        throw new IllegalStateException("Validated claim" +
                " does not contain " + claim + " claim.");
    }
}
