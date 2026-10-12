package com.panut.centralbanking.appsecuritycore.signature;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.http.HttpHeaders;

public class CoreHeaders {
    public static final String USER_AUTHORIZATION_HEADER = "Authorization";
    public static final String SERVICE_AUTHORIZATION_HEADER = "ServiceAuthorization";




    public static final Mono<Boolean> hasHeader(ServerWebExchange ex,
                                                String expectedHeader) {
        return Mono.fromCallable(() -> {
            return ex.getRequest()
                    .getHeaders()
                    .containsHeader(expectedHeader);
        });
    }

    public static final Mono<String> getExistTokenFromHeader(ServerWebExchange ex,
                                                String expectedHeader) {
        return Mono.defer(() -> {
            return hasHeader(ex, expectedHeader)
                    .filter(Boolean::booleanValue)
                    .switchIfEmpty(Mono.error(() -> new IllegalStateException(
                            "Token does not contain header: " + expectedHeader
                    )))
                    .thenReturn(ex.getRequest().getHeaders().getFirst(expectedHeader))
                    .filter(possibleToken ->
                            possibleToken.startsWith(CorePrefixes.AUTHORIZATION_TOKEN_PREFIXED))
                    .switchIfEmpty(Mono.error(() -> new IllegalStateException(
                            "Header does not started with: " + CorePrefixes.AUTHORIZATION_TOKEN_PREFIXED
                    )))
                    .filter(afterPrefix -> afterPrefix != null &&
                            !afterPrefix.isBlank())
                    .switchIfEmpty(Mono.error(() -> new IllegalStateException(
                            "Expecting token is empty."
                    )));

        });

    }

}
