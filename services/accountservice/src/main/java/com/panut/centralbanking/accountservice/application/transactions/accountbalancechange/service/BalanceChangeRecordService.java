package com.panut.centralbanking.accountservice.application.transactions.accountbalancechange.service;

import com.panut.centralbanking.accountservice.api.data.BalanceChangeData;
import com.panut.centralbanking.accountservice.application.transactions.accountbalancechange.BalanceChangeRepositoryManager;
import com.panut.centralbanking.accountservice.application.transactions.accountbalancechange.BalanceChangeConnectionFactory;
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
    private final BalanceChangeConnectionFactory connectionFactory;

    public BalanceChangeRecordService(BalanceChangeRepositoryManager balanceChangeRepositoryManager,
                                      BalanceChangeConnectionFactory connectionFactory) {
        this.balanceChangeRepositoryManager = balanceChangeRepositoryManager;
        this.connectionFactory = connectionFactory;
    }


    public Mono<Map<String,List<AccountBalanceChange>>> create(final List<BalanceChangeData> datas) {
        return Mono.defer(() -> {
                    return connectionFactory.batchInsert(datas);
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
