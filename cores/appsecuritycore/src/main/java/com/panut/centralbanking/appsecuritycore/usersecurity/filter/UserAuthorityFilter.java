package com.panut.centralbanking.appsecuritycore.usersecurity.filter;

import com.panut.centralbanking.appsecuritycore.autoconfig.PrimarySignatureSecretKeyAutoConfiguration;
import com.panut.centralbanking.appsecuritycore.signature.CoreHeaders;
import com.panut.centralbanking.appsecuritycore.signature.jwt.CoreClaims;
import com.panut.centralbanking.appsecuritycore.usersecurity.context.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
public class UserAuthorityFilter implements WebFilter, Ordered {
    private final PrimarySignatureSecretKeyAutoConfiguration config;

    public UserAuthorityFilter(PrimarySignatureSecretKeyAutoConfiguration primarySignatureSecretKeyAutoConfiguration) {
        this.config = primarySignatureSecretKeyAutoConfiguration;
    }

    @Override
    public int getOrder() {
        return -499;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return CoreHeaders.hasHeader(exchange, CoreHeaders.USER_AUTHORIZATION_HEADER)
                .flatMap(body -> {
                    if (!body.headerExists()) {
                        log.debug("UserAuthorityFilter request does not have expected header.");
                        return chain.filter(exchange);
                    }
                    log.debug("UserAuthorityFilter has expected header.");

                    return CoreHeaders.getExistTokenFromHeader(body)
                            .doOnNext(a -> log.debug("after CoreHeaders.getExistTokenFromHeader"))
                            .flatMap(possibleToken -> CoreClaims.toClaims(
                                    possibleToken, config.getApiKeyHMAC()))
                            .doOnNext(a -> log.debug("after CoreClaims.toClaims"))
                            .map(validated -> {
                                List<String> userAuthorities = CoreClaims
                                        .getCollection(validated,
                                                CoreClaims.USER_AUTHORITIES_CLAIM);
                                String username = CoreClaims
                                        .getValue(validated,
                                                CoreClaims.USER_NAME_CLAIM);

                                return new UserPrincipal(userAuthorities, username);
                            })
                            .doOnNext(a -> log.debug("after CoreClaims.getCollection" +
                                    " and CoreClaims.getValue and constructed new UserPrincipal"))
                            .map(userPrincipal ->
                                    new UsernamePasswordAuthenticationToken(
                                            userPrincipal,
                                            null,
                                            userPrincipal.getAuthorities()))
                            .doOnNext(a -> log.debug("after constructed" +
                                    " userPrincipal to new UsernamePasswordAuthenticationToken"))
                            .flatMap(authentication ->
                                    chain.filter(exchange)
                                            .contextWrite(
                                                    ReactiveSecurityContextHolder
                                                            .withAuthentication(authentication)
                                            ));

                });
    }
}
