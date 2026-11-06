package com.panut.centralbanking.transactionservice.application.property;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component("transactionBalanceClearingProperty")
@ConfigurationProperties(prefix = "app.events.transactions.clearing")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionBalanceClearingProperty {
    private int scheduleMinutes;
    private long scheduleMilliOffset;
    private int sizeLimit;



}
