package com.panut.centralbanking.accountservice.infrastructure.repository;

import com.panut.centralbanking.accountservice.infrastructure.model.Account;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AccountRepository
        extends ReactiveCrudRepository<Account, String> {
}
