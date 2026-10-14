package com.panut.centralbanking.customerservice.application;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.UUID;

@Component
public class CustomerDefaultPasswordGenerator {
    private final SecureRandom secureRandom;

    public CustomerDefaultPasswordGenerator(SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
    }



    public String simple(String from) {
        return simple() + from;
    }

    public String simple() {
        Long random = secureRandom.nextLong(1_000_000_000, 1_999_999_999);
        return String.valueOf(random);

    }
}
