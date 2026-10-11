package com.panut.centralbanking.appcore.exceptions;

public class Conflicted extends RuntimeException {
    public Conflicted(String message) {
        super(message);
    }
}
