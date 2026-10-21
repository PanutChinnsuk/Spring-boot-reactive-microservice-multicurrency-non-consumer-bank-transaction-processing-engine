package com.panut.centralbanking.transactionservice.application.ledgeraccount.implement;

import com.panut.centralbanking.appcore.common.response.CommonResult;
import com.panut.centralbanking.transactionservice.api.request.LedgerAccountCreateRequest;
import com.panut.centralbanking.transactionservice.application.ledgeraccount.LedgerAccountRepositoryManager;
import com.panut.centralbanking.transactionservice.application.ledgeraccount.query.LedgerAccountQuery;
import com.panut.centralbanking.transactionservice.application.ledgeraccount.service.LedgerAccountCreateService;
import com.panut.centralbanking.transactionservice.application.ledgeraccount.validate.LedgerAccountPersistValidator;
import com.panut.centralbanking.transactionservice.infrastructure.model.LedgerAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class LedgerAccountCreateServiceImpl implements LedgerAccountCreateService {
    private final LedgerAccountRepositoryManager manager;
    private final LedgerAccountQuery query;
    private final LedgerAccountPersistValidator persistValidator;

    public LedgerAccountCreateServiceImpl(LedgerAccountRepositoryManager manager,
                                          LedgerAccountQuery query,
                                          LedgerAccountPersistValidator persistValidator) {
        this.manager = manager;
        this.query = query;
        this.persistValidator = persistValidator;
    }


    @Override
    public Mono<CommonResult> createLedgerAccount(final LedgerAccountCreateRequest r) {
        return query.findExistNotThrow()
                .then(persistValidator.validateDuplicates(r.getCode()))
                .flatMap(code -> manager.save(LedgerAccount.builder()
                        .ledgerType(r.getLedgerType())
                        .code(code)
                        .name(r.getName())
                        .build()))
                .thenReturn(CommonResult.defaultSuccess("Successfully created ledger account."))
                .doOnSuccess(a -> log.info("Successfully created ledger account."))
                .doOnError(error -> log.error("Failed to create ledger account, {}",
                        error.getLocalizedMessage()));
    }
}
