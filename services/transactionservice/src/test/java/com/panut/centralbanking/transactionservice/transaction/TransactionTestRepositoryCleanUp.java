package com.panut.centralbanking.transactionservice.transaction;

import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionLineRepository;
import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionProfileRepository;
import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TransactionTestRepositoryCleanUp {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionProfileRepository transactionProfileRepository;

    @Autowired
    private TransactionLineRepository transactionLineRepository;

    public String transactionId;
    public List<String> transactionLineIds = new ArrayList<>();


    public void cleanUp() {
        log.info("transactionId for clean up is null: {}", Boolean.toString(transactionId == null));
        log.info("transactionLinesIds for clean up size: {}", transactionLineIds.size());
        transactionLineIds.forEach(id -> transactionLineRepository
                .deleteById(id).block());

        if (transactionId == null) return;
        transactionProfileRepository.deleteById(transactionId).block();
        transactionRepository.deleteById(transactionId).block();
        log.info("Clean up test data");

    }


}
