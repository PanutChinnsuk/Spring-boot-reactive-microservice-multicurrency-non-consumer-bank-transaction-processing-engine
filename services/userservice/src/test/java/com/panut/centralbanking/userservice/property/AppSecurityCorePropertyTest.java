package com.panut.centralbanking.userservice.property;

import com.panut.centralbanking.userservice.IntegrationDevTest;
import com.panut.centralbanking.userservice.infrastructure.config.appsecuritycore.AppSecurityCoreProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AppSecurityCorePropertyTest implements IntegrationDevTest {
    @Autowired
    private AppSecurityCoreProperty property;


    @Test
    public void shouldApply() {
        Assertions.assertNotNull(property.getCustomerServiceKey());
    }
}
