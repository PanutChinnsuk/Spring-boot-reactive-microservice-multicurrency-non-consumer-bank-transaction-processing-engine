package com.panut.centralbanking.accountservice.application.internalaccounts.branch;

import com.panut.centralbanking.accountservice.infrastructure.model.InternalAccountBranch;
import com.panut.centralbanking.accountservice.infrastructure.repository.InternalAccountBranchRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class InternalAccountBranchRepositoryManager {
    private final InternalAccountBranchRepository repo;

    public InternalAccountBranchRepositoryManager(InternalAccountBranchRepository internalAccountBranchRepository) {
        this.repo = internalAccountBranchRepository;
    }


    public Mono<InternalAccountBranch> save(final InternalAccountBranch internalAccountBranch) {
        return repo.save(internalAccountBranch);
    }
}
