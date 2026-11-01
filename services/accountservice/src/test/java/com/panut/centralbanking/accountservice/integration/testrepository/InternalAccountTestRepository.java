package com.panut.centralbanking.accountservice.integration.testrepository;


import com.panut.centralbanking.accountservice.core.InternalAccountType;
import com.panut.centralbanking.accountservice.core.InternalBranch;
import com.panut.centralbanking.accountservice.infrastructure.model.InternalAccount;
import com.panut.centralbanking.accountservice.infrastructure.repository.queryresult.InternalAccountQueryResult;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface InternalAccountTestRepository
        extends ReactiveCrudRepository<InternalAccount, String> {

    Mono<Void> deleteByAccountId(String accountId);



}
