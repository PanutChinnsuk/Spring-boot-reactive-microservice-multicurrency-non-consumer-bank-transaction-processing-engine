package com.panut.centralbanking.appcore.exceptions;

public class RateLimtExceeded extends RuntimeException {
    public RateLimtExceeded(String message) {
        super(message);
    }
}
