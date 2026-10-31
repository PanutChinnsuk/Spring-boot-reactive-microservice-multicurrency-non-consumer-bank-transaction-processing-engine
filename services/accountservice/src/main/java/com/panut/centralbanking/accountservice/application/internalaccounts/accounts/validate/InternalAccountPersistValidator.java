package com.panut.centralbanking.accountservice.application.internalaccounts.accounts.validate;

import com.panut.centralbanking.accountservice.core.InternalAccountType;
import com.panut.centralbanking.accountservice.core.InternalBranch;
import com.panut.centralbanking.accountservice.infrastructure.repository.InternalAccountRepository;
import com.panut.centralbanking.appcore.exceptions.Conflicted;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class InternalAccountPersistValidator {
    private final InternalAccountRepository repository;

    public InternalAccountPersistValidator(InternalAccountRepository repository) {
        this.repository = repository;
    }


    public Mono<Void> validateDuplicatesInBranch(final String accountId,
                                                   final String branchId,
                                                   final InternalAccountType internalAccountType) {
        return repository.alreadyExists(accountId, branchId,
                        internalAccountType)
                .flatMap(exist ->
                        Mono.error(
                                () -> new Conflicted("A single internal branch can't " +
                                        "have duplicated internal account.")
                        ))
                .then();
    }
}
