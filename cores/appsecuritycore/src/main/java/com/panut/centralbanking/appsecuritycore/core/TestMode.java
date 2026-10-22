package com.panut.centralbanking.appsecuritycore.core;

public class TestMode {
    public static final String SECRET_KEY_SALT = "saltSaltsaltSaltsaltSaltsaltSaltsaltSaltsaltSaltsaltSaltsaltSaltsaltSaltsaltSaltsaltSalt";



    public static final String toSalt(String any) {
        return any + SECRET_KEY_SALT;
    }
}
