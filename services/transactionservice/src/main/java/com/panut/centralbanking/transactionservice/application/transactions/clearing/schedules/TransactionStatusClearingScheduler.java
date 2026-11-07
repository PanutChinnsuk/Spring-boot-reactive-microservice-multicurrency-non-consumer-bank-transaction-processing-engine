package com.panut.centralbanking.transactionservice.application.transactions.clearing.schedules;

import com.panut.centralbanking.transactionservice.application.property.TransactionClearingProperty;
import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TransactionStatusClearingScheduler {
    private final TransactionRepository transactionRepository;
    private final TransactionClearingProperty property;


    public TransactionStatusClearingScheduler(TransactionRepository transactionRepository,
                                              TransactionClearingProperty property) {
        this.transactionRepository = transactionRepository;
        this.property = property;
    }


    @Scheduled(fixedDelayString = "#{(60000 * @transactionClearingProperty.getScheduleMinutes) + " +
            "@transactionClearingProperty.getScheduleMilliOffset}")
    public void clearPendingAuthorize() {
        System.out.println("clear authorize");
    }

    @Scheduled(fixedDelayString = "#{(120000 * @transactionClearingProperty.getScheduleMinutes) + " +
            "@transactionClearingProperty.getScheduleMilliOffset}")
    public void clearPendingPosted() {
        System.out.println("clear post");
    }

    @Scheduled(fixedDelayString = "#{(180000 * @transactionClearingProperty.getScheduleMinutes) + " +
            "@transactionClearingProperty.getScheduleMilliOffset}")
    public void clearPendingReverse() {
        System.out.println("clear reverse");
    }
}
