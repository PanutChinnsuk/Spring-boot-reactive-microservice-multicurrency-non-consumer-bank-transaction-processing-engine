package com.panut.centralbanking.accountservice.application.balancechangemodel;

import com.panut.centralbanking.accountservice.infrastructure.model.impact.AccountBalanceDeduct;
import com.panut.centralbanking.accountservice.infrastructure.repository.AccountBalanceDeductRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class BalanceDeductRepositoryManager {
    private final AccountBalanceDeductRepository accountBalanceDeductRepository;

    public BalanceDeductRepositoryManager(AccountBalanceDeductRepository accountBalanceDeductRepository) {
        this.accountBalanceDeductRepository = accountBalanceDeductRepository;
    }


    public Mono<AccountBalanceDeduct> save(final AccountBalanceDeduct accountBalanceDeduct) {
        return this.accountBalanceDeductRepository.save(accountBalanceDeduct);
    }

    public Mono<List<AccountBalanceDeduct>> saveAll(final List<AccountBalanceDeduct> deducts) {
        return this.accountBalanceDeductRepository.saveAll(deducts)
                .collectList();
    }
}
