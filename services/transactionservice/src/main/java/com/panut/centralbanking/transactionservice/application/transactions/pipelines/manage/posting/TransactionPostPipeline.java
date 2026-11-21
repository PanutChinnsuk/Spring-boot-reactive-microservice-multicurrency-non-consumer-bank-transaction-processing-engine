package com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.posting;

import com.panut.centralbanking.transactionservice.application.events.balancechange.ChangingBalance;
import com.panut.centralbanking.transactionservice.application.events.balancechange.service.BalanceChangeEventService;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.TransactionManagePipelineResult;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.repository.TransactionManageChain;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.repository.TransactionManagePipelineRepository;
import com.panut.centralbanking.transactionservice.application.transactions.transaction.TransactionRepositoryManager;
import com.panut.centralbanking.transactionservice.application.worker.WorkerService;
import com.panut.centralbanking.transactionservice.core.LedgerType;
import com.panut.centralbanking.transactionservice.core.TransactionStatus;
import com.panut.centralbanking.transactionservice.core.WorkerName;
import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionLine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.function.Function;


@Component
@Slf4j
public class TransactionPostPipeline {
    private final TransactionManagePipelineRepository repository;
    private final TransactionRepositoryManager manager;
    private final BalanceChangeEventService eventService;
    private final WorkerService workerService;



    public TransactionPostPipeline(TransactionManagePipelineRepository repository,
                                        TransactionRepositoryManager manager,
                                        BalanceChangeEventService eventService,
                                   WorkerService workerService) {
        this.repository = repository;
        this.manager = manager;
        this.eventService = eventService;
        this.workerService = workerService;
    }


    private final TransactionStatus REQUIRED_TRANSACTION_STATUS = TransactionStatus.AUTHORIZED;


    private void direct(final boolean isAssetExpense,
                        final boolean isDebit,
                        final boolean isCredit,
                        final TransactionLine line,
                        final TransactionManageChain chain) {
        if (isAssetExpense) {
            if (isCredit)
                chain.addBalanceChangeEvent(eventService.create(line,
                        ChangingBalance.reserveDeduct()));
            if (isDebit)
                chain.addBalanceChangeEvent(eventService.create(line,
                        ChangingBalance.balanceAdd()));

        }else {
            if (isCredit)
                chain.addBalanceChangeEvent(eventService.create(line,
                        ChangingBalance.balanceAdd()));
            if (isDebit)
                chain.addBalanceChangeEvent(eventService.create(line,
                        ChangingBalance.reserveDeduct()));
        }
    }


    private Mono<TransactionManageChain> clear(final TransactionManageChain chain) {
        return chain.getTransactionLines()
                .map(line -> {
                    var transactionType = chain.getTransaction().getTransactionType();
                    LedgerType ledgerType = chain.getLedgerTypeOfLine(line);
                    boolean isAssetExpense = ledgerType.isAssetExpense();

                    var direction = line.getDirection();
                    boolean isDebit = direction.isDebit();
                    boolean isCredit = direction.isCredit();
                    boolean isDebitOverdraft = direction.isDebitOverdraft();
                    boolean isCreditOverdraft = direction.isCreditOverdraft();

                    ChangingBalance changingBalance = ChangingBalance.none();

                    if (isAssetExpense) {
                        if (isCredit || isCreditOverdraft) changingBalance = ChangingBalance.reserveDeduct();
                        if (isDebit) changingBalance = ChangingBalance.balanceAdd();
                        if (isDebitOverdraft) changingBalance = ChangingBalance.overdraftedAdd();

                    }else {
                        if (isCredit) changingBalance = ChangingBalance.balanceAdd();
                        if (isDebit || isDebitOverdraft) changingBalance = ChangingBalance.reserveDeduct();
                        if (isCreditOverdraft) changingBalance = ChangingBalance.overdraftedAdd();

                    }

                    chain.addBalanceChangeEvent(eventService.create(line, changingBalance));

                    return line;
                })
                .thenMany(eventService.flush(chain.getBalanceChangeEvents()))
                .then(Mono.just(chain));

    }


    public Mono<TransactionManagePipelineResult> post(final String transactionId) {
        return workerService.initWorker(WorkerName.TRANSACTION)
                .then(repository.fullTransaction(transactionId, REQUIRED_TRANSACTION_STATUS))
                .doOnNext(a -> log.info("after get full transaction data chain"))
                .flatMap(this::clear)
                .doOnNext(a -> log.info("after clear"))
                .map(TransactionManageChain::getTransaction)
                .flatMap(transaction -> manager
                        .save(transaction.upNextTransactionStatus()))
                .doOnNext(a -> log.info("update current transaction with upnext status"))
                .map(transaction -> new TransactionManagePipelineResult()
                        .transaction(transaction))
                .doOnSuccess(a -> log.info("Successfully pending posted " +
                        "transaction."))
                .doOnError(error ->
                        log.error("Failed to pending posted] transaction, {}",
                                error.getLocalizedMessage()));
    }
}
