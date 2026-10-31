package com.panut.centralbanking.accountservice.integration.property;

import com.panut.centralbanking.accountservice.IntegrationDev;
import com.panut.centralbanking.accountservice.application.property.InternalAccountProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class InternalAccountPropertyTest implements IntegrationDev {
    @Autowired
    private InternalAccountProperty internalAccountProperty;



    @Test
    public void shouldApplied() {
        Assertions
                .assertNotNull(internalAccountProperty.getCurrentBankBranch());
    }
}
