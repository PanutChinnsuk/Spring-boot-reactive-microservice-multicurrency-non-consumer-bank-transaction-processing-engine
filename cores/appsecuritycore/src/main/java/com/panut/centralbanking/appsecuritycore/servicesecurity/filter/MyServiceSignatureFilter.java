package com.panut.centralbanking.appsecuritycore.servicesecurity.filter;

import com.panut.centralbanking.appsecuritycore.autoconfig.MyServiceSignatureConfig;
import com.panut.centralbanking.appsecuritycore.servicesecurity.context.ServiceSecurityContext;
import com.panut.centralbanking.appsecuritycore.signature.CoreHeaders;
import com.panut.centralbanking.appsecuritycore.signature.jwt.CoreClaims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Slf4j
public class MyServiceSignatureFilter implements WebFilter, Ordered {
    private final MyServiceSignatureConfig myServiceSignatureConfig;

    public MyServiceSignatureFilter(MyServiceSignatureConfig myServiceSignatureConfig) {
        this.myServiceSignatureConfig = myServiceSignatureConfig;
    }

    @Override
    public int getOrder() {
        return -500;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return CoreHeaders
                .hasHeader(exchange, CoreHeaders.SERVICE_AUTHORIZATION_HEADER,
                        CoreHeaders.USER_AUTHORIZATION_HEADER)
                .flatMap(body -> {
                    if (!body.headerExists()) {
                        log.debug("MyServiceSignatureFilter request has no expected header.");
                        return chain.filter(exchange);
                    }
                    log.debug("MyServiceSignatureFilter request has expected header.");
                    return CoreHeaders.getExistTokenFromHeader(body)
                            .doOnNext(a -> log.debug("after CoreHeaders.getExistTokenFromHeader"))
                            .flatMap(token -> CoreClaims.toClaims(
                                    token, myServiceSignatureConfig))
                            .doOnNext(a -> log.debug("after CoreClaims.toClaims"))
                            .flatMap(validated -> CoreClaims
                                    .getCollections(validated, CoreClaims.SERVICE_AUTHORITIES_CLAIM))
                            .doOnNext(a -> log.debug("after CoreClaims.getCollections"))
                            .map(authorities -> new ServiceSecurityContext(
                                    authorities))
                            .doOnNext(a -> log.debug("after constructed" +
                                    " new ServiceSecurityContext"))
                            .flatMap(serviceCtx -> {
                                if (body.isUserCallRequest()) {
                                    log.debug("MyServiceSignatureFilter set Reactive" +
                                            "SecurityContextHolder with " +
                                            "authenticated security context.");
                                    UsernamePasswordAuthenticationToken authToken
                                           = new UsernamePasswordAuthenticationToken("principal",
                                            null, Collections.emptyList());

                                    return withAuthentication(serviceCtx, chain,
                                            exchange, authToken);
                                }
                                log.debug("MyServiceSignatureFilter set Reactive" +
                                        "SecurityContextHolder with no " +
                                        "security context.");


                                return withNoAuthentication(serviceCtx, chain,
                                        exchange);
                            });
                });

    }

    private Mono<Void> withNoAuthentication(ServiceSecurityContext serviceCtx,
                                     WebFilterChain chain,
                                     ServerWebExchange exchange) {
        return chain.filter(exchange)
                .contextWrite(ctx ->
                        ctx.put(ServiceSecurityContext.class,
                                serviceCtx));
    }

    private Mono<Void> withAuthentication(ServiceSecurityContext serviceCtx,
                                               WebFilterChain chain,
                                               ServerWebExchange exchange,
                                               Authentication authentication) {
        return withNoAuthentication(serviceCtx, chain, exchange)
                .contextWrite(ReactiveSecurityContextHolder
                        .withAuthentication(authentication));
    }
}
