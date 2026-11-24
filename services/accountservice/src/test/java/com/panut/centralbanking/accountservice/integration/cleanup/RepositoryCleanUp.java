package com.panut.centralbanking.accountservice.integration.cleanup;


import com.panut.centralbanking.accountservice.infrastructure.repository.AccountProfileRepository;
import com.panut.centralbanking.accountservice.infrastructure.repository.AccountRepository;
import com.panut.centralbanking.accountservice.integration.testrepository.AccountBalanceChangeTestRepository;
import com.panut.centralbanking.accountservice.integration.testrepository.AccountBalanceTestRepository;
import com.panut.centralbanking.accountservice.integration.testrepository.AccountOverdraftTestRepository;
import com.panut.centralbanking.appcore.integrationtest.SeededDatabaseIdFlag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class RepositoryCleanUp {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountBalanceTestRepository balanceTestRepository;

    @Autowired
    private AccountBalanceChangeTestRepository balanceChangeTestRepository;

    @Autowired
    private AccountProfileRepository profileRepository;

    @Autowired
    private AccountOverdraftTestRepository overdraftTestRepository;


    public void hardDelete(List<String> accountIds) {
        balanceChangeTestRepository.deleteByAccountIdIn(accountIds).block();

        accountIds.forEach(id -> {
            overdraftTestRepository.deleteByAccountId(id,
                    SeededDatabaseIdFlag.NONE).block();
            profileRepository.deleteById(id).block();
            balanceTestRepository.deleteById(id).block();
            accountRepository.deleteById(id).block();

        });
    }


    public void hardDelete(String... accountIds) {
        hardDelete(Arrays.stream(accountIds).toList());
    }
}
