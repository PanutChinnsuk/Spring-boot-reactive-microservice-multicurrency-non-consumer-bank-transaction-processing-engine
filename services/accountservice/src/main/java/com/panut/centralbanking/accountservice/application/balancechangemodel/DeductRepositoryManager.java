package com.panut.centralbanking.accountservice.application.balancechangemodel;

import com.panut.centralbanking.accountservice.infrastructure.model.impact.AccountBalanceDeduct;
import com.panut.centralbanking.accountservice.infrastructure.repository.AccountBalanceDeductRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class DeductRepositoryManager {
    private final AccountBalanceDeductRepository accountBalanceDeductRepository;

    public DeductRepositoryManager(AccountBalanceDeductRepository accountBalanceDeductRepository) {
        this.accountBalanceDeductRepository = accountBalanceDeductRepository;
    }


    public Mono<AccountBalanceDeduct> save(final AccountBalanceDeduct accountBalanceDeduct) {
        return this.accountBalanceDeductRepository.save(accountBalanceDeduct);
    }
}
