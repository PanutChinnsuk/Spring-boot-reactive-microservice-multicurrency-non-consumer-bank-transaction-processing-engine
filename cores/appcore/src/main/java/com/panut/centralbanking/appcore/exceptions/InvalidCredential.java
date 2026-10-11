package com.panut.centralbanking.appcore.exceptions;

public class InvalidCredential extends RuntimeException {
    public InvalidCredential(String message) {
        super(message);
    }
}
