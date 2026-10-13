package com.panut.centralbanking.customerservice.application.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
@ConfigurationProperties(prefix = "app.customer.useractivationcode")
@Data
public class CustomerUserActivationCodeProperty {
    private int expireMinutes;

    public LocalDateTime getExpiry() {
        return LocalDateTime.now().plus(
                expireMinutes, ChronoUnit.MINUTES
        );
    }

}
