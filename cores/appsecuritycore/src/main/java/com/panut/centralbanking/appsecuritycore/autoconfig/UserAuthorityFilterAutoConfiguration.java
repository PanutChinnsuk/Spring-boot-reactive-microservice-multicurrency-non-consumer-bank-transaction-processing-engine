package com.panut.centralbanking.appsecuritycore.autoconfig;

import com.panut.centralbanking.appsecuritycore.usersecurity.filter.UserAuthorityFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@Slf4j
public class UserAuthorityFilterAutoConfiguration {

    @Bean
    public UserAuthorityFilter userAuthorityFilter(PrimarySignatureSecretKeyAutoConfiguration primarySignatureSecretKeyAutoConfiguration) {
        log.info("Auto config bean userAuthorityFilter");
        return new UserAuthorityFilter(primarySignatureSecretKeyAutoConfiguration);
    }
}
