package com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.authorization;

import com.panut.centralbanking.appcore.exceptions.SystemMisused;
import com.panut.centralbanking.transactionservice.application.events.balancechange.ChangingBalance;
import com.panut.centralbanking.transactionservice.application.events.balancechange.service.BalanceChangeEventService;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.TransactionManagePipelineResult;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.repository.TransactionManageChain;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.repository.TransactionManagePipelineRepository;
import com.panut.centralbanking.transactionservice.core.LedgerType;
import com.panut.centralbanking.transactionservice.core.TransactionStatus;
import com.panut.centralbanking.transactionservice.infrastructure.model.Transaction;
import com.panut.centralbanking.transactionservice.infrastructure.model.events.BalanceChangeEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class TransactionAuthorizePipeline {
    private final TransactionManagePipelineRepository repository;
    private final BalanceChangeEventService eventService;


    public TransactionAuthorizePipeline(TransactionManagePipelineRepository repository,
                                        BalanceChangeEventService eventService) {
        this.repository = repository;
        this.eventService = eventService;
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
                    eventService.create(line, ChangingBalance.balanceDeduct());
                    eventService.create(line, ChangingBalance.reserveAdd());


                    return line;
                })
                .then(Mono.just(chain));
    }


    public Mono<TransactionManagePipelineResult> authorize(final String transactionId) {
        return repository.fullTransaction(transactionId, REQUIRED_TRANSACTION_STATUS)
                .flatMap(chain -> clear(chain))
                .map(chain ->
                        new TransactionManagePipelineResult()
                                .transaction(chain.getTransaction()));
    }
}
