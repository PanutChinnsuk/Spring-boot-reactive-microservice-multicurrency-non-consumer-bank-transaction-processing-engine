package com.panut.centralbanking.accountservice.infrastructure.repository;


import com.panut.centralbanking.accountservice.infrastructure.model.InternalAccount;
import com.panut.centralbanking.accountservice.infrastructure.repository.queryresult.AccountInfoForTransactionQueryResult;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.Set;

public interface InternalAccountRepository
        extends ReactiveCrudRepository<InternalAccount, String> {




}
