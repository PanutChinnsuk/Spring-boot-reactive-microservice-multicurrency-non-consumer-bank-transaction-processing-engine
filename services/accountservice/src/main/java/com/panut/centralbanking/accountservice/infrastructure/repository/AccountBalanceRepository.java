package com.panut.centralbanking.accountservice.infrastructure.repository;

import com.panut.centralbanking.accountservice.infrastructure.model.AccountBalance;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AccountBalanceRepository
        extends ReactiveCrudRepository<AccountBalance, String> {
}
