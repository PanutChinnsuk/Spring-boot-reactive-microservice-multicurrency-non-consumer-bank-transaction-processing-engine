package com.panut.centralbanking.transactionservice.property;

import com.panut.centralbanking.transactionservice.IntegrationDevTest;
import com.panut.centralbanking.transactionservice.application.property.TransactionBalanceClearingProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TransactionBalanceClearingPropertyTest implements IntegrationDevTest {
    @Autowired
    private TransactionBalanceClearingProperty property;



    @Test
    public void shouldApply() {
        Assertions.assertTrue(property.getScheduleMinutes() > 0);
        Assertions.assertTrue(property.getScheduleMilliOffset() > 0);
        Assertions.assertTrue(property.getSizeLimit() > 0);
    }
}
