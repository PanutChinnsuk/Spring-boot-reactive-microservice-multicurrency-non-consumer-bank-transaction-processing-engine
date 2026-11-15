package com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.posting;

import com.panut.centralbanking.transactionservice.application.events.balancechange.ChangingBalance;
import com.panut.centralbanking.transactionservice.application.events.balancechange.service.BalanceChangeEventService;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.TransactionManagePipelineResult;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.repository.TransactionManageChain;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.repository.TransactionManagePipelineRepository;
import com.panut.centralbanking.transactionservice.application.transactions.transaction.TransactionRepositoryManager;
import com.panut.centralbanking.transactionservice.core.LedgerType;
import com.panut.centralbanking.transactionservice.core.TransactionStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class TransactionPostPipeline {
    private final TransactionManagePipelineRepository repository;
    private final TransactionRepositoryManager manager;
    private final BalanceChangeEventService eventService;



    public TransactionPostPipeline(TransactionManagePipelineRepository repository,
                                        TransactionRepositoryManager manager,
                                        BalanceChangeEventService eventService) {
        this.repository = repository;
        this.manager = manager;
        this.eventService = eventService;
    }


    private final TransactionStatus REQUIRED_TRANSACTION_STATUS = TransactionStatus.AUTHORIZED;



    private Mono<TransactionManageChain> clear(final TransactionManageChain chain) {
        return chain.getTransactionLines()
                .map(line -> {
                    LedgerType ledgerType = chain.getLedgerTypeOfLine(line);
                    boolean isCredit = line.getDirection().isCredit();
                    boolean isDebit = line.getDirection().isDebit();
                    boolean isAssetExpense = ledgerType.isAssetExpense();

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


                    return line;
                })
                .thenMany(eventService.flush(chain.getBalanceChangeEvents()))
                .then(Mono.just(chain));

    }


    public Mono<TransactionManagePipelineResult> post(final String transactionId) {
        return repository.fullTransaction(transactionId, REQUIRED_TRANSACTION_STATUS)
                .flatMap(this::clear)
                .map(TransactionManageChain::getTransaction)
                .map(transaction -> new TransactionManagePipelineResult()
                        .transaction(transaction));
    }
}
