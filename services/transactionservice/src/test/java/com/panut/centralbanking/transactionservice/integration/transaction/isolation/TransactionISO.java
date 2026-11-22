package com.panut.centralbanking.transactionservice.integration.transaction.isolation;

import com.panut.centralbanking.transactionservice.core.FxDirection;
import com.panut.centralbanking.transactionservice.core.TransactionLineDirection;
import com.panut.centralbanking.transactionservice.core.TransactionStatus;
import com.panut.centralbanking.transactionservice.core.TransactionType;
import com.panut.centralbanking.transactionservice.infrastructure.model.Transaction;
import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionLine;
import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionProfile;
import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionLineRepository;
import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionProfileRepository;
import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TransactionISO {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionProfileRepository transactionProfileRepository;

    @Autowired
    private TransactionLineRepository transactionLineRepository;




    public Transaction transaction(TransactionType transactionType,
                              TransactionStatus transactionStatus) {
        var tx = Transaction.builder()
                .transactionType(transactionType)
                .transactionStatus(transactionStatus)
                .build();

        return transactionRepository.save(tx).block();
    }

    public Transaction transaction() {
        return transaction(TransactionType.TRANSFER, null);
    }

    public Transaction transaction(TransactionType transactionType) {
        return transaction(transactionType, null);
    }


    public TransactionProfile transactionProfile(Transaction transaction,
                                                 FxDirection fxDirection,
                                                 String fxRateId) {
        var tp = TransactionProfile.builder()
                .transactionId(transaction.getId())
                .fxDirection(fxDirection)
                .fxRateId(fxRateId)
                .build();

        return transactionProfileRepository.save(tp).block();
    }

    public TransactionProfile transactionProfile(Transaction transaction) {
        return transactionProfile(transaction, FxDirection.NONE, null);
    }

    public TransactionLine transactionLine(Transaction transaction,
                                           String accountId,
                                           BigDecimal baseAmount,
                                           BigDecimal converted,
                                           TransactionLineDirection direction,
                                           String ledgerAccountId) {
        var line = TransactionLine.builder()
                .transactionId(transaction.getId())
                .accountId(accountId)
                .basedAmount(baseAmount)
                .convertedAmount(converted)
                .direction(direction)
                .ledgerAccountId(ledgerAccountId)
                .build();

        return transactionLineRepository.save(line).block();

    }


}
