package com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.authorization;

import com.panut.centralbanking.appcore.exceptions.SystemMisused;
import com.panut.centralbanking.transactionservice.application.events.balancechange.ChangingBalance;
import com.panut.centralbanking.transactionservice.application.events.balancechange.service.BalanceChangeEventService;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.TransactionManagePipelineResult;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.authorization.validator.TransactionAuthorizationPipelineValidator;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.repository.TransactionManageChain;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.repository.TransactionManagePipelineRepository;
import com.panut.centralbanking.transactionservice.application.transactions.transaction.TransactionRepositoryManager;
import com.panut.centralbanking.transactionservice.application.worker.WorkerService;
import com.panut.centralbanking.transactionservice.core.LedgerType;
import com.panut.centralbanking.transactionservice.core.TransactionStatus;
import com.panut.centralbanking.transactionservice.core.WorkerName;
import com.panut.centralbanking.transactionservice.infrastructure.model.Transaction;
import com.panut.centralbanking.transactionservice.infrastructure.model.events.BalanceChangeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@Slf4j
public class TransactionAuthorizePipeline {
    private final TransactionManagePipelineRepository repository;
    private final TransactionRepositoryManager manager;
    private final BalanceChangeEventService eventService;
    private final WorkerService workerService;
    private final TransactionAuthorizationPipelineValidator validator;



    public TransactionAuthorizePipeline(TransactionManagePipelineRepository repository,
                                        TransactionRepositoryManager manager,
                                        BalanceChangeEventService eventService,
                                        WorkerService workerService,
                                        TransactionAuthorizationPipelineValidator validator) {
        this.repository = repository;
        this.manager = manager;
        this.eventService = eventService;
        this.workerService = workerService;
        this.validator = validator;
    }

    private final TransactionStatus REQUIRED_TRANSACTION_STATUS = TransactionStatus.PENDING;

    private Mono<TransactionManageChain> clear(final TransactionManageChain chain) {
        return chain.getTransactionLines()
                .filter(line -> {
                    LedgerType ledgerType = chain.getLedgerTypeOfLine(line);
                    boolean isAssetExpense = ledgerType.isAssetExpense();
                    boolean isCredit = line.getDirection().isCredit();
                    boolean isDebit = line.getDirection().isDebit();

                    return (isAssetExpense && isCredit) || (!isAssetExpense && isDebit);

                })
                .map(line -> {
                    chain.addBalanceChangeEvent(eventService.create(line,
                            ChangingBalance.balanceDeduct()));
                    chain.addBalanceChangeEvent(eventService.create(line,
                            ChangingBalance.reserveAdd()));

                    log.info("account: {} deduct balance to reserve",
                            line.getAccountId());
                    return line;
                })
                .thenMany(eventService.flush(chain.getBalanceChangeEvents()))
                .then(Mono.just(chain));
    }


    public Mono<TransactionManagePipelineResult> authorize(final String transactionId) {
        return workerService.initWorker(WorkerName.TRANSACTION)
                .then(repository.fullTransaction(transactionId, REQUIRED_TRANSACTION_STATUS))
                .doOnNext(a -> log.info("after get full transaction data chain"))
                .flatMap(chain -> {
                    var profile = chain.getTransactionProfile();
                    if (profile.isReversedState()) {
                        return validator.isReversingTransactionCleared(profile
                                .getReversedOfTransactionId())
                                .thenReturn(chain);
                    }

                    return Mono.just(chain);
                })
                .flatMap(this::clear)
                .doOnNext(a -> log.info("after clear"))
                .map(TransactionManageChain::getTransaction)
                .flatMap(transaction -> manager
                        .save(transaction.upNextTransactionStatus()))
                .doOnNext(a -> log.info("update current transaction with upnext status"))
                .map(transaction ->
                        new TransactionManagePipelineResult()
                                .transaction(transaction))
                .doOnSuccess(a -> log.info("Successfully pending authorize " +
                        "transaction."))
                .doOnError(error ->
                        log.error("Failed to pending authorize transaction, {}",
                                error.getLocalizedMessage()));
    }
}
