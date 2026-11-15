package com.panut.centralbanking.transactionservice.integration.transaction.clearing.manager;

import com.panut.centralbanking.appcore.integrationtest.SeededDatabase;
import com.panut.centralbanking.transactionservice.api.service.account.data.related.BalanceChangeType;
import com.panut.centralbanking.transactionservice.api.service.account.data.related.BalanceImpact;
import com.panut.centralbanking.transactionservice.application.events.balancechange.BalanceChangeEventRepositoryManager;
import com.panut.centralbanking.transactionservice.infrastructure.model.events.BalanceChangeEvent;
import com.panut.centralbanking.transactionservice.testrepository.BalanceChangeEventTestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class TestEventManager {
    @Autowired
    private BalanceChangeEventRepositoryManager manager;

    @Autowired
    private BalanceChangeEventTestRepository testRepository;


    private List<String> toDelete = new ArrayList<>();

    public String create(String transactionId) {
        String thisId = UUID.randomUUID().toString();

        manager.saveAll(List.of(
                BalanceChangeEvent.builder()
                        .id(thisId)
                        .balanceChangeType(BalanceChangeType.BALANCE)
                        .amount(BigDecimal.ONE)
                        .balanceImpact(BalanceImpact.ADD)
                        .accountId(SeededDatabase.ID_NORMAL_ACCOUNT_1)
                        .transactionId(transactionId)
                        .build()
        )).collectList().block();

        toDelete.add(thisId);

        return thisId;
    }

    public BalanceChangeEvent get(String id) {
        return testRepository.findById(id).block();
    }

    public void delete(String id) {
        testRepository.deleteById(id).block();
    }

    public void clear() {
        if (toDelete.isEmpty())
            return;
        toDelete.forEach(a -> delete(a));
    }
}
