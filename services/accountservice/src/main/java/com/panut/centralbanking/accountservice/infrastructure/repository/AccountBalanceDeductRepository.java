package com.panut.centralbanking.accountservice.infrastructure.repository;

import com.panut.centralbanking.accountservice.infrastructure.model.impact.AccountBalanceDeduct;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AccountBalanceDeductRepository
        extends ReactiveCrudRepository<AccountBalanceDeduct, String> {
}
