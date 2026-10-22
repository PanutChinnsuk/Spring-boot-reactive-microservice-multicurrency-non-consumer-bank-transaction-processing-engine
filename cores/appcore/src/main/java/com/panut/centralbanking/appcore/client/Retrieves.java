package com.panut.centralbanking.appcore.client;

import com.panut.centralbanking.appcore.common.response.CommonResult;
import com.panut.centralbanking.appcore.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

@Slf4j
public class Retrieves {



    private static Throwable stupid(final String message,
                                                    final Class<? extends RuntimeException> any) {
        if (any == Forbidden.class)
            return new Forbidden(message);

        if (any == BusinessException.class)
            return new BusinessException(message);

        if (any == Conflicted.class)
            return new Conflicted(message);

        if (any == InvalidCredential.class)
            return new InvalidCredential(message);

        if (any == NotFound.class)
            return new NotFound(message);

        if (any == RateLimtExceeded.class)
            return new RateLimtExceeded(message);

        if (any == SystemMisused.class)
            return new SystemMisused(message);

        if (any == Unauthorized.class)
            return new Unauthorized(message);


        return new Exception(message);
    }

    private static final <T> Mono<T> error(final CommonResult result,
                                           final Class<? extends RuntimeException> any,
                                           final String name) {
        log.error("error {}, {}", name, result.getOptionalMessage());
        return Mono.error(() -> stupid(result.getOptionalMessage(), any));
    }





    public static final <T>Mono<CommonResult<T>> doRequest(final WebClient webClient,
                                                           final String fullPath,
                                                           final Object body,
                                                           final Class<? extends RuntimeException> onError,
                                                           final String name,
                                                           final ParameterizedTypeReference<CommonResult<T>> reference) {
        return webClient
                .post()
                .uri(fullPath)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(reference)
                .flatMap(result -> {
                    if (!result.isSuccess()) error(result,
                            onError, name);
                    return Mono.just(result);
                });
    }

    public static final Mono<CommonResult> doRequest(final WebClient webClient,
                                                           final String fullPath,
                                                           final Object body,
                                                           final Class<? extends RuntimeException> onError,
                                                           final String name) {
        return webClient
                .post()
                .uri(fullPath)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(CommonResult.class)
                .flatMap(result -> {
                    if (!result.isSuccess()) error(result,
                            onError, name);
                    return Mono.just(result);
                });
    }

    public static final <T>Mono<T> doRequest(final WebClient webClient,
                                                           final String fullPath,
                                                           final Object body,
                                                           final Class<? extends RuntimeException> onError,
                                                           final String name,
                                                           final Class<T> expectedClass) {
        return webClient
                .post()
                .uri(fullPath)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(expectedClass);
    }
}
