package com.panut.centralbanking.transactionservice.integration.transaction.cleanrepo;

import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionLineRepository;
import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionProfileRepository;
import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionRepository;
import com.panut.centralbanking.transactionservice.testrepository.BalanceChangeEventTestRepository;
import com.panut.centralbanking.transactionservice.testrepository.TransactionLineTestRepository;
import com.panut.centralbanking.transactionservice.testrepository.TransactionProfileTestRepository;
import com.panut.centralbanking.transactionservice.testrepository.TransactionTestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TransactionTestRepositoryCleanUp {
    @Autowired
    private TransactionTestRepository transactionTestRepository;

    @Autowired
    private TransactionProfileTestRepository profileTestRepository;

    @Autowired
    private TransactionLineTestRepository lineTestRepository;

    @Autowired
    private BalanceChangeEventTestRepository eventTestRepository;

    public List<String> transactionIds = new ArrayList<>();
    public List<String> transactionLineIds = new ArrayList<>();


    public void cleanUp() {
        transactionLineIds.forEach(id -> lineTestRepository
                .deleteById(id).block());

        transactionIds.forEach(id -> {
            profileTestRepository.deleteById(id).block();
            transactionTestRepository.deleteById(id).block();
        });



    }

    public void cleanUp(List<String> transactionIds) {



        transactionIds.forEach(id -> {
            var lines = lineTestRepository
                    .findByTransactionId(id).collectList().block();

            lineTestRepository.deleteAll(lines).block();


            profileTestRepository.deleteById(id).block();
            transactionTestRepository.deleteById(id).block();
        });


    }

    public void cleanUpEvents(List<String> transactionIds) {



        transactionIds.forEach(id -> {
            eventTestRepository
                    .deleteByTransactionId(id).block();
        });


    }


}
