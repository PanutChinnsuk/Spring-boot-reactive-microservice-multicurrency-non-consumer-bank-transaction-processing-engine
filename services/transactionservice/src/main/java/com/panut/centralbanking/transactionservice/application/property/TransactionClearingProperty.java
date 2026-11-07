package com.panut.centralbanking.transactionservice.application.property;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component("transactionClearingProperty")
@ConfigurationProperties(prefix = "app.events.transactions.clearing")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionClearingProperty {
    private int scheduleMinutes;
    private int scheduleMilliOffset;
    private int sizeLimit;



}
