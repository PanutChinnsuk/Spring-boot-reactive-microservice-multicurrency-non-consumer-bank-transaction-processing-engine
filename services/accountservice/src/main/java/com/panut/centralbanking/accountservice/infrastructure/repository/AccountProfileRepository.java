package com.panut.centralbanking.accountservice.infrastructure.repository;

import com.panut.centralbanking.accountservice.infrastructure.model.AccountProfile;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AccountProfileRepository
        extends ReactiveCrudRepository<AccountProfile, String> {
}
