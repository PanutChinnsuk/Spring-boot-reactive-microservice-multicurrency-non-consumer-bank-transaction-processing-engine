package com.panut.centralbanking.accountservice.infrastructure.repository;

import com.panut.centralbanking.accountservice.infrastructure.model.AccountBalanceChange;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AccountBalanceChangeRepository
        extends ReactiveCrudRepository<AccountBalanceChange, String> {
}
