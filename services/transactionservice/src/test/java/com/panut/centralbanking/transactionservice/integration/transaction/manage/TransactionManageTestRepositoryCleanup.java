package com.panut.centralbanking.transactionservice.integration.transaction.manage;

import com.panut.centralbanking.transactionservice.core.TransactionStatus;
import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionLineRepository;
import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionRepository;
import com.panut.centralbanking.transactionservice.testrepository.BalanceChangeEventTestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransactionManageTestRepositoryCleanup {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BalanceChangeEventTestRepository balanceChangeEventTestRepository;


    public void setDefault(String transactionId) {
        transactionRepository.save(
                transactionRepository.findById(transactionId)
                        .block().changeStatus(TransactionStatus.PENDING)
        ).block();
    }

    public void cleanUpBalanceChangeEvents(String transactionId) {
        balanceChangeEventTestRepository.deleteByTransactionId(transactionId).block();
    }
}
