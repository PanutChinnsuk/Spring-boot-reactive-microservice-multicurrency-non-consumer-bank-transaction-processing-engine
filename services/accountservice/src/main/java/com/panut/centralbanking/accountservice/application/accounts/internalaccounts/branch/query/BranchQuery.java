package com.panut.centralbanking.accountservice.application.internalaccounts.branch.query;

import com.panut.centralbanking.accountservice.core.InternalBranch;
import com.panut.centralbanking.accountservice.infrastructure.repository.InternalAccountBranchRepository;
import com.panut.centralbanking.appcore.exceptions.NotFound;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class BranchQuery {
    private final InternalAccountBranchRepository repo;

    public BranchQuery(InternalAccountBranchRepository repo) {
        this.repo = repo;
    }


    public Mono<String> getBranchId(final InternalBranch branch) {
        return repo.getBranchIdByBranch(branch)
                .switchIfEmpty(Mono.error(
                        () -> new NotFound("Internal account branches table " +
                                "does not contain a branch of " + branch)
                ));

    }
}
