package com.panut.centralbanking.transactionservice.integration.transaction.cleanrepo;

import com.panut.centralbanking.transactionservice.testrepository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class RepositoryCleanUp {
    @Autowired
    private TransactionTestRepository transactionTestRepository;

    @Autowired
    private TransactionProfileTestRepository profileTestRepository;

    @Autowired
    private TransactionLineTestRepository lineTestRepository;

    @Autowired
    private BalanceChangeEventTestRepository eventTestRepository;

    @Autowired
    private TransactionOverdraftContentTestRepository overdraftContentTestRepository;

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

    public void deleteByListOrder(List<String> any) {
        any.forEach(a -> {
            profileTestRepository.deleteById(a).block();
        });
    }

    public void hardCleanUp(String... transactionIds) {
        hardCleanUp(Arrays.stream(transactionIds).toList());
    }

    public void hardCleanUp(List<String> transactionIds) {
        cleanUpEvents(transactionIds);
        cleanUpOverdraftContents(transactionIds);


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

    public void cleanUpOverdraftContents(List<String> transactionIds) {
        transactionIds.forEach(id -> {
            overdraftContentTestRepository
                    .deleteByTransactionId(id).block();
        });
    }


}
