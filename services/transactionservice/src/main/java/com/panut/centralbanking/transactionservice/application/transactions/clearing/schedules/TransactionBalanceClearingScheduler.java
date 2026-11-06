package com.panut.centralbanking.transactionservice.application.transactions.clearing.schedules;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TransactionBalanceClearingScheduler {



    @Scheduled(fixedDelayString = "#{(60000 * @transactionBalanceClearingProperty.getScheduleMinutes) " +
            "+ @transactionBalanceClearingProperty.getScheduleMilliOffset}")
    public void flush() {
        System.out.println("flush");
    }
}
