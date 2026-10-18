package com.panut.centralbanking.accountservice.application.balancechangemodel.service;

import com.panut.centralbanking.accountservice.api.data.BalanceChangeData;
import com.panut.centralbanking.accountservice.application.balancechangemodel.BalanceAddRepositoryManager;
import com.panut.centralbanking.accountservice.infrastructure.model.Account;
import com.panut.centralbanking.accountservice.infrastructure.model.impact.AccountBalanceAdd;
import com.panut.centralbanking.accountservice.infrastructure.model.impact.BalanceChangeModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
public class BalanceAddCreateService {
    private final BalanceAddRepositoryManager balanceAddRepositoryManager;

    public BalanceAddCreateService(BalanceAddRepositoryManager balanceAddRepositoryManager) {
        this.balanceAddRepositoryManager = balanceAddRepositoryManager;
    }


    public Mono<List<AccountBalanceAdd>> create(final Account account,
                                          final List<BalanceChangeData> datas) {
        return Mono.defer(() -> {
                    return balanceAddRepositoryManager.saveAll(datas
                            .stream().map(add ->
                                    AccountBalanceAdd.builder()
                                            .accountId(account.getId())
                                            .addedAt(add.getAt())
                                            .amount(add.getAmount())
                                            .balanceChangeType(add.getBalanceChangeType())
                                            .transactionId(add.getTransactionId())
                                            .build()).toList());
                })
                .doOnSuccess(a -> log.info("Created " +
                        "account balance adding records."))
                .doOnError(error ->
                        log.error("Failed to create account balance adding records {}",
                        error.getLocalizedMessage()));
    }
}
