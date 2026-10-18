package com.panut.centralbanking.accountservice.infrastructure.repository;

import com.panut.centralbanking.accountservice.infrastructure.model.impact.AccountBalanceAdd;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AccountBalanceAddRepository
        extends ReactiveCrudRepository<AccountBalanceAdd, String> {
}
