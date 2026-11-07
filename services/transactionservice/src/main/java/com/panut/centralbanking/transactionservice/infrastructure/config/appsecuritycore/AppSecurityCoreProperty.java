package com.panut.centralbanking.transactionservice.infrastructure.config.appsecuritycore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.security.core")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppSecurityCoreProperty {
    private String fxRateServiceKey;
    private String accountServiceKey;


}
