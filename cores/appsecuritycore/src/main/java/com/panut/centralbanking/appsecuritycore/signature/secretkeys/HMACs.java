package com.panut.centralbanking.appsecuritycore.signature.secretkeys;

import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class HMACs {


    public static SecretKey stringToSecretKey(String mySecretKey) {
        Objects.requireNonNull(mySecretKey);

        return Keys.hmacShaKeyFor(mySecretKey.getBytes(StandardCharsets.UTF_8));
    }
}
