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


    public Mono<List<AccountBalanceChange>> create(final List<AccountBalanceChange> records) {
        return Mono.defer(() -> {
                    return connectionFactory.batchInsert(records);
                })
                .doOnSuccess(a -> log.info("Created " +
                        "account balance adding records."))
                .doOnError(error ->
                        log.error("Failed to create account balance adding records {}",
                        error.getLocalizedMessage()));
    }
}
