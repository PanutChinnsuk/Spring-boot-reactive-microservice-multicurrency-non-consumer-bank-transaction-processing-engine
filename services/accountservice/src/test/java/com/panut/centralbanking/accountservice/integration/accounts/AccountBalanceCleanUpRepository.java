package com.panut.centralbanking.accountservice.integration.accounts;

import com.panut.centralbanking.accountservice.application.accounts.account.query.AccountQuery;
import com.panut.centralbanking.accountservice.application.transactions.accountbalancechange.service.BalanceChangeService;
import com.panut.centralbanking.accountservice.core.BalanceChangeType;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountBalance;
import com.panut.centralbanking.accountservice.infrastructure.repository.AccountBalanceRepository;
import com.panut.centralbanking.accountservice.infrastructure.repository.ProcessedBalanceChangeEventRepository;
import com.panut.centralbanking.accountservice.integration.testrepository.AccountBalanceTestRepository;
import com.panut.centralbanking.accountservice.integration.testrepository.ProcessedBalanceChangeEventTestRepository;
import com.panut.centralbanking.appcore.integrationtest.SeededDatabase;
import com.panut.centralbanking.appcore.integrationtest.SeededDatabaseIdFlag;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class AccountBalanceCleanUpRepository {


    @Autowired
    private AccountBalanceTestRepository testRepository;

    @Autowired
    private ProcessedBalanceChangeEventTestRepository procTestRepository;

    private List<String> accountIds = new ArrayList<>();


    private void resetProcessedEvent() {
        procTestRepository.deleteByTestFlag(SeededDatabaseIdFlag.TEST)
                .block();

        log.info("clean up processed account balance change test");
    }

    private void resetAccountBalance() {
        if (accountIds.isEmpty())
            return;

        testRepository.setDefaultBalance(
                BigDecimal.valueOf(100),
                accountIds
        ).block();

        log.info("clean up account balance test");
    }


    public void setBack(String... accountIds) {
        log.info("pre clean up account id");
        Arrays.stream(accountIds).forEach(id -> {
            log.info("added account id for clean up");
            this.accountIds
                    .add(id);
        });

        resetAccountBalance();
        resetProcessedEvent();
    }
}
