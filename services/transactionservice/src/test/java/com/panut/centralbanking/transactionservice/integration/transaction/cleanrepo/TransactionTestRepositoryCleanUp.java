package com.panut.centralbanking.transactionservice.integration.transaction.cleanrepo;

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

    public List<String> transactionIds = new ArrayList<>();
    public List<String> transactionLineIds = new ArrayList<>();


    public void cleanUp() {
        log.info("transactionId for clean up size: {}", transactionIds.size());
        log.info("transactionLinesIds for clean up size: {}", transactionLineIds.size());
        transactionLineIds.forEach(id -> transactionLineRepository
                .deleteById(id).block());

        transactionIds.forEach(id -> {
            transactionProfileRepository.deleteById(id).block();
            transactionRepository.deleteById(id).block();
        });

        log.info("Clean up test data");

    }


}
