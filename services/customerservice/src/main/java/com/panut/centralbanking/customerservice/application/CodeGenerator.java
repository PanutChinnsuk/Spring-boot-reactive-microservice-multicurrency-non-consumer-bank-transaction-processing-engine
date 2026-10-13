package com.panut.centralbanking.customerservice.application;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class CodeGenerator {
    private final SecureRandom secureRandom;

    public CodeGenerator(SecureRandom secureRandom) {
        this.secureRandom = secureRandom;
    }


    public String generateSimple() {
        return String.valueOf(secureRandom.nextLong(100_000, 999_999));
    }
}
