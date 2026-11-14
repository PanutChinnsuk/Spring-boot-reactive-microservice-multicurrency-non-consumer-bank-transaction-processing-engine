package com.panut.centralbanking.transactionservice.application.property;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component("transactionClearingProperty")
@ConfigurationProperties(prefix = "app.events.transactions.clearing")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionClearingProperty {
    private int blacklistMinutes;
    private int applyingBlacklistMinutes;
    private int scheduleMinutes;
    private int scheduleMilliOffset;
    private int sizeLimit;
    private int applyingSizeLimit;


    public LocalDateTime getNewBlacklistExpiry() {
        return LocalDateTime.now().plusMinutes(this.blacklistMinutes);
    }

    public LocalDateTime getNewApplyingBlacklistExpiry() {
        return LocalDateTime.now().plusMinutes(this.applyingBlacklistMinutes);
    }

}
