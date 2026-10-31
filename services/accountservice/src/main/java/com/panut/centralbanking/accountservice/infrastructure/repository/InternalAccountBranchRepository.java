package com.panut.centralbanking.accountservice.infrastructure.repository;

import com.panut.centralbanking.accountservice.core.InternalBranch;
import com.panut.centralbanking.accountservice.infrastructure.model.InternalAccountBranch;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface InternalAccountBranchRepository extends
        ReactiveCrudRepository<InternalAccountBranch, String> {


    @Query("""
            SELECT b.id FROM internal_account_branches b
            WHERE b.branch = :branch
            FOR UPDATE
            """)
    Mono<String> getBranchIdByBranch(@Param("branch") InternalBranch branch);
}
