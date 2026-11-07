package com.panut.centralbanking.accountservice.application.accounts.accountoverdraft.implement;

import com.panut.centralbanking.accountservice.api.data.AccountOverdraftCreateData;
import com.panut.centralbanking.accountservice.application.accounts.accountoverdraft.AccountOverdraftRepositoryManager;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountOverdraft;
import com.panut.centralbanking.appcore.common.response.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class AccountOverdraftServiceImpl {
    private final AccountOverdraftRepositoryManager manager;

    public AccountOverdraftServiceImpl(AccountOverdraftRepositoryManager manager) {
        this.manager = manager;
    }


    public Mono<CommonResult> overdraft(final AccountOverdraftCreateData data) {
        return manager.save(AccountOverdraft
                .builder()
                .transactionId(data.getTransactionId())
                .accountId(data.getAccountId())
                .amount(data.getAmount())
                .overdraftedAt(data.getOverdraftedAt()).build())
                .thenReturn(CommonResult.defaultSuccess())
                .doOnSuccess(a -> log.info("Successfully overdrafted account."))
                .onErrorResume(error -> Mono
                        .just(CommonResult
                                .defaultFailed(error.getLocalizedMessage())));
    }
}
