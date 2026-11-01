package com.panut.centralbanking.accountservice.integration.testrepository;

import com.panut.centralbanking.accountservice.core.InternalBranch;
import com.panut.centralbanking.accountservice.infrastructure.model.InternalAccountBranch;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface InternalAccountBranchTestRepository extends
        ReactiveCrudRepository<InternalAccountBranch, String> {


    Mono<Void> deleteByInternalBranch(InternalBranch internalBranch);

}
