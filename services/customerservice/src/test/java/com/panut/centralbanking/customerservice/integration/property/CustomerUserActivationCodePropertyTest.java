package com.panut.centralbanking.customerservice.integration.property;

import com.panut.centralbanking.customerservice.application.property.CustomerUserActivationCodeProperty;
import com.panut.centralbanking.customerservice.IntegrationDev;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CustomerUserActivationCodePropertyTest implements IntegrationDev {
    @Autowired
    private CustomerUserActivationCodeProperty userActivationCodeProperty;





    @Test
    public void shouldApplied() {
        Assertions.assertTrue(
                userActivationCodeProperty.getExpireMinutes() > 0
        );
    }
}
