package com.panut.centralbanking.fxrateservice.integration;


import com.panut.centralbanking.fxrateservice.IntegrationDev;
import com.panut.centralbanking.fxrateservice.application.property.FxRateProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class FxRatePropertyTest implements IntegrationDev {
    @Autowired
    private FxRateProperty fxRateProperty;




    @Test
    public void shouldSetProperty() {
        Assertions.assertTrue(fxRateProperty.getAddingSafeGuardRangeSeconds() > 0);
    }


}
