package com.panut.centralbanking.customerservice.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain security(ServerHttpSecurity http) {
        return http
                .csrf(csrf -> csrf.disable())
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(auth -> auth
                        .anyExchange().authenticated())
                .formLogin(login -> login.disable())
                .logout(logout -> logout.disable())
                .build();
    }
}
