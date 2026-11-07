package com.panut.centralbanking.accountservice.application.account.implement;

import com.panut.centralbanking.accountservice.api.data.AccountBalanceCreateData;
import com.panut.centralbanking.accountservice.api.data.AccountProfileCreateData;
import com.panut.centralbanking.accountservice.api.response.AccountCreatedData;
import com.panut.centralbanking.accountservice.application.account.AccountRepositoryManager;
import com.panut.centralbanking.accountservice.application.account.service.AccountCreateService;
import com.panut.centralbanking.accountservice.application.accountbalance.service.AccountBalanceCreateService;
import com.panut.centralbanking.accountservice.application.accountprofile.service.AccountProfileCreateService;
import com.panut.centralbanking.accountservice.infrastructure.model.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@Transactional(propagation = Propagation.REQUIRED)
public class AccountCreateServiceImpl implements AccountCreateService {
    private final AccountRepositoryManager manager;
    private final AccountProfileCreateService accountProfileCreateService;
    private final AccountBalanceCreateService accountBalanceCreateService;


    public AccountCreateServiceImpl(AccountRepositoryManager manager,
                                    AccountProfileCreateService accountProfileCreateService,
                                    AccountBalanceCreateService accountBalanceCreateService) {
        this.manager = manager;
        this.accountProfileCreateService = accountProfileCreateService;
        this.accountBalanceCreateService = accountBalanceCreateService;
    }


    @Override
    public Mono<AccountCreatedData> createAccount(final String customerId,
                                                  final AccountProfileCreateData profileData,
                                                  final AccountBalanceCreateData balanceData) {
        return manager.save(Account.builder().customerId(customerId).build())
                .flatMap(account -> new AccountCreatedData().account(account))
                .doOnNext(a -> log.info("Created account mono chain."))
                .flatMap(data -> accountProfileCreateService
                        .createAccountProfile(data.getAccountId(), profileData)
                        .flatMap(profile -> data.accountProfile(profile)))
                .flatMap(data -> accountBalanceCreateService
                        .createAccountBalance(data.getAccountId(), balanceData)
                        .flatMap(balance -> data.accountBalance(balance)))
                .doOnSuccess(a -> log
                        .info("Successfully created account for customer."))
                .doOnError(error -> log.error("Failed to create account for customer, {}",
                        error.getLocalizedMessage()));
    }
}
