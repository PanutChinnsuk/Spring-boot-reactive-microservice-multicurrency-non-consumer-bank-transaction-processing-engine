package com.panut.centralbanking.transactionservice.integration.transaction.isolation;

import com.panut.centralbanking.transactionservice.infrastructure.model.Transaction;
import com.panut.centralbanking.transactionservice.infrastructure.model.datacontent.TransactionOverdraftContent;
import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionOverdraftContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OverdraftContentISO {
    @Autowired
    private TransactionOverdraftContentRepository contentRepository;


    public TransactionOverdraftContent transactionOverdraftContent(Transaction transaction,
                                                                   String overdraftId) {
        var ct = TransactionOverdraftContent
                .builder()
                .transactionId(transaction.getId())
                .overdraftId(overdraftId)
                .build();

        return contentRepository.save(ct).block();
    }
}
