package com.panut.centralbanking.accountservice.application.balancechangemodel.service;

import com.panut.centralbanking.accountservice.api.data.BalanceChangeData;
import com.panut.centralbanking.accountservice.application.balancechangemodel.BalanceAddRepositoryManager;
import com.panut.centralbanking.accountservice.application.balancechangemodel.BalanceDeductRepositoryManager;
import com.panut.centralbanking.accountservice.infrastructure.model.Account;
import com.panut.centralbanking.accountservice.infrastructure.model.impact.AccountBalanceAdd;
import com.panut.centralbanking.accountservice.infrastructure.model.impact.AccountBalanceDeduct;
import com.panut.centralbanking.accountservice.infrastructure.model.impact.BalanceChangeModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
public class BalanceDeductCreateService {
    private final BalanceDeductRepositoryManager balanceDeductRepositoryManager;

    public BalanceDeductCreateService(BalanceDeductRepositoryManager balanceDeductRepositoryManager) {
        this.balanceDeductRepositoryManager = balanceDeductRepositoryManager;
    }


    public Mono<List<AccountBalanceDeduct>> create(final Account account,
                                    final List<BalanceChangeData> datas) {
        return Mono.defer(() -> {
            return balanceDeductRepositoryManager.saveAll(datas.stream()
                    .map(deduct -> AccountBalanceDeduct.builder()
                            .accountId(account.getId())
                            .deductedAt(deduct.getAt())
                            .amount(deduct.getAmount())
                            .balanceChangeType(deduct.getBalanceChangeType())
                            .transactionId(deduct.getTransactionId())
                            .build()).toList());
                })
                .doOnSuccess(a -> log.info("Created " +
                        "account balance adding records."))
                .doOnError(error ->
                        log.error("Failed to create account balance adding records {}",
                                error.getLocalizedMessage()));

    }
}
