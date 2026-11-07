package com.panut.centralbanking.accountservice.application.internalaccounts.branch.implement;

import com.panut.centralbanking.accountservice.api.request.useronly.InternalAccountBranchAddRequest;
import com.panut.centralbanking.accountservice.application.internalaccounts.branch.InternalAccountBranchRepositoryManager;
import com.panut.centralbanking.accountservice.application.internalaccounts.branch.service.BranchService;
import com.panut.centralbanking.accountservice.infrastructure.model.InternalAccountBranch;
import com.panut.centralbanking.appcore.common.response.CommonResult;
import com.panut.centralbanking.appcore.exceptions.Conflicted;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@Transactional(propagation = Propagation.REQUIRED)
@Slf4j
//LOW STAKE OPERATIONS
public class BranchServiceImpl implements BranchService {
    private final InternalAccountBranchRepositoryManager manager;

    public BranchServiceImpl(InternalAccountBranchRepositoryManager manager) {
        this.manager = manager;
    }

    @Override
    public Mono<CommonResult> addBranch(final InternalAccountBranchAddRequest r) {
        return manager.save(InternalAccountBranch.builder()
                .internalBranch(r.getBranch())
                .build())
                .map(branch -> CommonResult.defaultSuccess("Successfully created " +
                        "internal account branch of " + branch.getInternalBranch()))
                .doOnSuccess(a -> log.info("Successfully created " +
                        "internal account branch."))
                .onErrorMap(DataIntegrityViolationException.class,
                        error -> new Conflicted("Branch of " + r.getBranch()
                + " already exists."))
                .doOnError(error ->
                        log.error("Failed to create internal account branch, {}",
                        error.getLocalizedMessage()));
    }
}
