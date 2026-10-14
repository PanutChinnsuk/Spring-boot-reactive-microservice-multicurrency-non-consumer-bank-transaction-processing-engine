package com.panut.centralbanking.appsecuritycore.encoder;


import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class BCrypts {
    private final PasswordEncoder passwordEncoder;

    public BCrypts(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }


    public Mono<String> encode(String raw) {
        return Mono.fromCallable(() -> {
            return passwordEncoder.encode(raw);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Boolean> matched(String raw, String hash) {
        return Mono.fromCallable(() -> {
            return passwordEncoder.matches(raw, hash);
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
