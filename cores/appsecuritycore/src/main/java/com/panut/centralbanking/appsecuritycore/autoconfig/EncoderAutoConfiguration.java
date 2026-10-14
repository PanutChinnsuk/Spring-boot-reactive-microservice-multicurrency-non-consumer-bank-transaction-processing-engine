package com.panut.centralbanking.appsecuritycore.autoconfig;

import com.panut.centralbanking.appsecuritycore.encoder.BCrypts;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@AutoConfiguration
public class EncoderAutoConfiguration {

    @Bean("bcryptEncoder")
    public PasswordEncoder bcryptEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public BCrypts bCrypts(@Qualifier("bcryptEncoder") PasswordEncoder bcryptEncoder) {
        return new BCrypts(bcryptEncoder);
    }
}
