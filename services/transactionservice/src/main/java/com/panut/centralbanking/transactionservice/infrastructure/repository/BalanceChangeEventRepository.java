package com.panut.centralbanking.transactionservice.infrastructure.repository;

import com.panut.centralbanking.transactionservice.infrastructure.model.events.BalanceChangeEvent;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface BalanceChangeEventRepository
        extends ReactiveCrudRepository<BalanceChangeEvent, String> {
}
