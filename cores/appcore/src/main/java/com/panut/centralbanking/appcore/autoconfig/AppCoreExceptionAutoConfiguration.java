package com.panut.centralbanking.appcore.autoconfig;

import com.panut.centralbanking.appcore.exceptions.handler.GlobalMonoExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class AppCoreExceptionAutoConfiguration {


    @Bean
    public GlobalMonoExceptionHandler globalMonoExceptionHandler() {
        return new GlobalMonoExceptionHandler();
    }
}
