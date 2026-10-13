package com.panut.centralbanking.appsecuritycore.signature;

import com.panut.centralbanking.appsecuritycore.signature.body.CoreHeadersBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Slf4j
public class CoreHeaders {
    public static final String USER_AUTHORIZATION_HEADER = "Authorization";
    public static final String SERVICE_AUTHORIZATION_HEADER = "ServiceAuthorization";




    public static final Mono<CoreHeadersBody> hasHeader(ServerWebExchange ex,
                                                        String expectedHeader,
                                                        String... userRelatedHeaders) {
        return Mono.fromCallable(() -> {
            HttpHeaders httpHeaders = ex.getRequest().getHeaders();
            boolean userCallRequest = userRelatedHeaders.length <= 0
                    ? true
                    : Arrays.stream(userRelatedHeaders)
                    .anyMatch(related -> httpHeaders
                            .containsHeader(related));
            log.debug("is userCallRequest: {}", userCallRequest);

            return new CoreHeadersBody(httpHeaders.containsHeader(expectedHeader),
                    userCallRequest,
                    httpHeaders.getFirst(expectedHeader));
        });
    }

    public static final Mono<String> getExistTokenFromHeader(CoreHeadersBody coreHeadersBody) {
        return Mono.defer(() -> {
            return Mono.just(coreHeadersBody)
                    .map(body -> body.getExpectedHeader())
                    .filter(possibleToken -> possibleToken != null &&
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
