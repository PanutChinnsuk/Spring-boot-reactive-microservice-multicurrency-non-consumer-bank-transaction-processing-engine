package com.panut.centralbanking.transactionservice.property;

import com.panut.centralbanking.transactionservice.IntegrationDevTest;
import com.panut.centralbanking.transactionservice.application.property.TransactionClearingProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TransactionClearingPropertyTest implements IntegrationDevTest {
    @Autowired
    private TransactionClearingProperty property;



    @Test
    public void shouldApply() {
        Assertions.assertTrue(property.getScheduleMinutes() > 0);
        Assertions.assertTrue(property.getScheduleMilliOffset() > 0);
        Assertions.assertTrue(property.getSizeLimit() > 0);
    }
}
