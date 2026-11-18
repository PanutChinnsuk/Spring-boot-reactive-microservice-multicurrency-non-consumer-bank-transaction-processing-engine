package com.panut.centralbanking.appcore.exceptions;

public class CriticalFailureException extends RuntimeException {
    public CriticalFailureException(String message) {
        super(message);
    }
}
