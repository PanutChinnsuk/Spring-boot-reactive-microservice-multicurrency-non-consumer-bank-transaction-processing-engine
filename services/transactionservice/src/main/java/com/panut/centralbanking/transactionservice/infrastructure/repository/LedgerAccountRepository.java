package com.panut.centralbanking.transactionservice.infrastructure.repository;

import com.panut.centralbanking.transactionservice.infrastructure.model.LedgerAccount;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface LedgerAccountRepository extends ReactiveCrudRepository<LedgerAccount, String> {
}
