package com.panut.centralbanking.accountservice.integration.connection;

import com.panut.centralbanking.accountservice.IntegrationDevTest;
import com.panut.centralbanking.accountservice.application.accounts.accountbalance.AccountBalanceConnectionFactory;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountBalance;
import com.panut.centralbanking.accountservice.infrastructure.repository.AccountBalanceRepository;
import com.panut.centralbanking.accountservice.integration.accounts.AccountBalanceCleanUpRepository;
import com.panut.centralbanking.appcore.integrationtest.SeededDatabase;
import io.r2dbc.spi.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class AccountBalanceConnectionFactoryTest implements IntegrationDevTest {
    @Autowired
    private AccountBalanceConnectionFactory connectionFactory;

    @Autowired
    private AccountBalanceRepository accountBalanceRepository;

    @Autowired
    private AccountBalanceCleanUpRepository cleanRepo;

    String accountId1 = SeededDatabase.ID_NORMAL_ACCOUNT_1;
    String accountId2 = SeededDatabase.ID_NORMAL_ACCOUNT_2;
    String accountId3 = SeededDatabase.ID_INTERNAL_ACCOUNT_1;
    String accountId4 = SeededDatabase.ID_INTERNAL_ACCOUNT_2;

    @Test
    public void shouldBatchUpdate() {
        Set<String> accountIds = Set.of(accountId1, accountId2,
                accountId3, accountId4);

        List<AccountBalance> list = accountBalanceRepository
                .findTransactionAccountBalances(accountIds
                ).collectList().block();



        Assertions.assertEquals(4, list.size());

       connectionFactory.batchUpdate(list).block();



        Map<String, AccountBalance> post = accountBalanceRepository
                .findTransactionAccountBalances(accountIds)
                .collectList()
                .block()
                .stream()
                .collect(Collectors.toMap(
                        a -> a.getId(),
                        a -> a
                ));

        list.forEach(ab -> {
            Assertions.assertEquals(ab.getVersion() + 1,
                    post.get(ab.getId()).getVersion(),
                    ab.getId());
        });
    }
}
