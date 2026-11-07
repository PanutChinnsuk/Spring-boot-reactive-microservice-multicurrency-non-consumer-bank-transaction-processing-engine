package com.panut.centralbanking.accountservice.application.transactions.accountbalancechange.service;

import com.panut.centralbanking.accountservice.api.data.BalanceChangeData;
import com.panut.centralbanking.accountservice.application.transactions.accountbalancechange.BalanceChangeRepositoryManager;
import com.panut.centralbanking.accountservice.infrastructure.model.AccountBalanceChange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class BalanceChangeRecordService {
    private final BalanceChangeRepositoryManager balanceChangeRepositoryManager;

    public BalanceChangeRecordService(BalanceChangeRepositoryManager balanceChangeRepositoryManager) {
        this.balanceChangeRepositoryManager = balanceChangeRepositoryManager;
    }


    public Mono<Map<String,List<AccountBalanceChange>>> create(final Map<String,String> accountMap,
                                                   final List<BalanceChangeData> datas) {
        return Mono.defer(() -> {
                    return balanceChangeRepositoryManager.saveAll(datas
                            .stream().map(change ->
                                    AccountBalanceChange.builder()
                                            .accountId(accountMap.get(
                                                    change.getAccountId()))
                                            .changedAt(change.getCreatedAt())
                                            .amount(change.getAmount())
                                            .balanceImpact(change.getBalanceImpact())
                                            .balanceChangeType(change.getBalanceChangeType())
                                            .transactionId(change.getTransactionId())
                                            .build()).toList());
                })
                .map(list -> {
                    log.info("mapping unsorted account balance change records " +
                            "into map<accountId, List<AccountBalanceChange>>");
                    Map<String, List<AccountBalanceChange>> map = new HashMap<>();

                    list.forEach(record -> {
                        List<AccountBalanceChange> currentList = map
                                .computeIfAbsent(record.getAccountId(),
                                        id -> new ArrayList<>());

                        currentList.add(record);

                    });
                    log.info("after mapped unsorted, map size: {}",
                            map.size());

                    return map;
                })
                .doOnSuccess(a -> log.info("Created " +
                        "account balance adding records."))
                .doOnError(error ->
                        log.error("Failed to create account balance adding records {}",
                        error.getLocalizedMessage()));
    }
}
