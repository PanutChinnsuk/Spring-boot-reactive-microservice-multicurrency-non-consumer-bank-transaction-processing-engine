package com.panut.centralbanking.accountservice.application.internalaccounts.accounts.implement;

import com.panut.centralbanking.accountservice.api.request.useronly.InternalAccountAddRequest;
import com.panut.centralbanking.accountservice.api.request.useronly.InternalAccountIdRequest;
import com.panut.centralbanking.accountservice.application.internalaccounts.accounts.InternalAccountRepositoryManager;
import com.panut.centralbanking.accountservice.application.internalaccounts.accounts.service.InternalAccountService;
import com.panut.centralbanking.accountservice.application.internalaccounts.accounts.validate.InternalAccountPersistValidator;
import com.panut.centralbanking.accountservice.application.internalaccounts.branch.query.BranchQuery;
import com.panut.centralbanking.accountservice.core.InternalAccountType;
import com.panut.centralbanking.accountservice.core.InternalBranch;
import com.panut.centralbanking.accountservice.infrastructure.model.InternalAccount;
import com.panut.centralbanking.accountservice.infrastructure.repository.InternalAccountRepository;
import com.panut.centralbanking.appcore.common.response.CommonResult;
import com.panut.centralbanking.appcore.exceptions.BusinessException;
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
public class InternalAccountServiceImpl implements InternalAccountService {
    private final InternalAccountRepositoryManager manager;
    private final InternalAccountPersistValidator persistValidator;
    private final BranchQuery branchQuery;

    public InternalAccountServiceImpl(InternalAccountRepositoryManager manager,
                                      InternalAccountPersistValidator persistValidator,
                                      BranchQuery branchQuery) {
        this.manager = manager;
        this.persistValidator = persistValidator;
        this.branchQuery = branchQuery;
    }


    @Override
    public Mono<CommonResult> add(final String accountId,
                                  final InternalBranch branch,
                                  final InternalAccountType internalAccountType) {
        return branchQuery.getBranchId(branch)
                .flatMap(branchId ->
                        persistValidator.validateDuplicatesInBranch(
                                accountId,
                                branchId,
                                internalAccountType)
                        .then(manager.save(InternalAccount.builder()
                                                .accountId(accountId)
                                                .branchId(branchId)
                                                .internalAccountType(internalAccountType)
                                                .build()))
                )
                .thenReturn(CommonResult.defaultSuccess("Successfully added internal account."))
                .doOnError(error ->
                        log.error("Failed to add internal account, {}",
                                error.getLocalizedMessage()))
                .onErrorMap(DataIntegrityViolationException.class,
                        err -> new BusinessException("Account must not " +
                        "be internal and id must be exist for internal account adding."));

    }

    @Override
    public Mono<CommonResult> hardDelete(final InternalAccountIdRequest r) {
        return manager
                .deleteById(r.getAccountId())
                .thenReturn(CommonResult.defaultSuccess("Successfully deleted internal account."))
                .doOnError(error ->
                        log.error("Failed to delete internal account, {}",
                                error.getLocalizedMessage()))
                .onErrorMap(err -> new BusinessException("Account must " +
                        "be internal and id must be exist for internal account delete."));
    }
}
