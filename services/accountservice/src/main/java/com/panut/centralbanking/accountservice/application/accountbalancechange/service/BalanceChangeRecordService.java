package com.panut.centralbanking.accountservice.application.accountbalancechange.service;

import com.panut.centralbanking.accountservice.api.data.BalanceChangeData;
import com.panut.centralbanking.accountservice.application.accountbalancechange.BalanceChangeRepositoryManager;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountBalanceChange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class BalanceChangeRecordService {
    private final BalanceChangeRepositoryManager balanceChangeRepositoryManager;

    public BalanceChangeRecordService(BalanceChangeRepositoryManager balanceChangeRepositoryManager) {
        this.balanceChangeRepositoryManager = balanceChangeRepositoryManager;
    }


    public Mono<List<AccountBalanceChange>> create(final Map<String,String> accountMap,
                                                   final List<BalanceChangeData> datas) {
        return Mono.defer(() -> {
                    return balanceChangeRepositoryManager.saveAll(datas
                            .stream().map(change ->
                                    AccountBalanceChange.builder()
                                            .accountId(accountMap.get(
                                                    change.getAccountId()))
                                            .changedAt(change.getAt())
                                            .amount(change.getAmount())
                                            .balanceImpact(change.getBalanceImpact())
                                            .balanceChangeType(change.getBalanceChangeType())
                                            .transactionId(change.getTransactionId())
                                            .build()).toList());
                })
                .doOnSuccess(a -> log.info("Created " +
                        "account balance adding records."))
                .doOnError(error ->
                        log.error("Failed to create account balance adding records {}",
                        error.getLocalizedMessage()));
    }
}
