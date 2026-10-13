package com.panut.centralbanking.appsecuritycore.autoconfig;

import com.panut.centralbanking.appsecuritycore.servicesecurity.filter.MyServiceSignatureFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@Slf4j
public class MyServiceSignatureFilterAutoConfiguration {


    @Bean
    public MyServiceSignatureFilter myServiceSignatureFilter(MyServiceSignatureAutoConfiguration myServiceSignatureAutoConfiguration) {
        log.info("Auto config bean myServiceSignatureFilter");
        return new MyServiceSignatureFilter(myServiceSignatureAutoConfiguration);
    }
}
