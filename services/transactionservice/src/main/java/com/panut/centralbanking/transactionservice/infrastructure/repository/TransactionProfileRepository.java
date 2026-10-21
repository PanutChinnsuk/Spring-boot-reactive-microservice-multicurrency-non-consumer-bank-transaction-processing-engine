package com.panut.centralbanking.transactionservice.infrastructure.repository;

import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionProfile;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface TransactionProfileRepository extends ReactiveCrudRepository<TransactionProfile, String> {
}
