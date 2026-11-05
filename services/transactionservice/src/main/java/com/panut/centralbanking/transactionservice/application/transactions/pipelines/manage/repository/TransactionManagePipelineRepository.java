package com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.repository;

import com.panut.centralbanking.transactionservice.application.events.balancechange.ChangingBalance;
import com.panut.centralbanking.transactionservice.application.ledgeraccount.query.LedgerAccountQuery;
import com.panut.centralbanking.transactionservice.application.transactions.line.query.TransactionLineQuery;
import com.panut.centralbanking.transactionservice.application.transactions.profile.query.TransactionProfileQuery;
import com.panut.centralbanking.transactionservice.application.transactions.transaction.query.TransactionQuery;
import com.panut.centralbanking.transactionservice.core.LedgerType;
import com.panut.centralbanking.transactionservice.core.TransactionStatus;
import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionLine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TransactionManagePipelineRepository {
    private final TransactionQuery transactionQuery;
    private final TransactionProfileQuery transactionProfileQuery;
    private final TransactionLineQuery transactionLineQuery;
    private final LedgerAccountQuery ledgerAccountQuery;

    public TransactionManagePipelineRepository(TransactionQuery transactionQuery,
                                               TransactionProfileQuery transactionProfileQuery,
                                               TransactionLineQuery transactionLineQuery,
                                               LedgerAccountQuery ledgerAccountQuery) {
        this.transactionQuery = transactionQuery;
        this.transactionProfileQuery = transactionProfileQuery;
        this.transactionLineQuery = transactionLineQuery;
        this.ledgerAccountQuery = ledgerAccountQuery;
    }




    public Mono<TransactionManageChain> justTransaction(final String transactionId,
                                                        final TransactionStatus expectedStatus) {
        return transactionQuery.findById(transactionId, expectedStatus)
                .doOnNext(a -> log.info("after find transaction with id and expectedStatus"))
                .flatMap(transaction -> new TransactionManageChain()
                        .transaction(transaction))
                .doOnNext(a -> log.info("start a transaction manage chain and " +
                        "put transaction into chain"))
                .flatMap(chain -> transactionProfileQuery
                        .findTransactionProfileById(transactionId)
                        .doOnNext(a -> log.info("after find transaction lines with " +
                                "previous transaction id"))
                        .flatMap(profile -> chain.transactionProfile(profile))
                        .doOnNext(a -> log.info("put transaction profile " +
                                "into chain"))
                );
    }


    public Mono<TransactionManageChain> fullTransaction(final String transactionId,
                                                        final TransactionStatus expectedStatus) {
        return justTransaction(transactionId, expectedStatus)
                .doOnNext(a -> log.info("called justTransaction for fullTransaction"))
                .flatMap(chain -> transactionLineQuery
                        .findTransactionLines(transactionId)
                        .doOnNext(a -> log.info("after find transaction lines"))
                        .as(chain::transactionLines)
                        .doOnNext(a -> log.info("put transaction lines (flux) " +
                                "into chain")
                        )
                )
                .flatMap(chain -> ledgerAccountQuery
                        .getLedgerTypeMapByTransactionId(transactionId)
                        .doOnNext(a -> log.info("get ledger type map"))
                        .flatMap(map -> chain.ledgerTypeMap(map))
                        .doOnNext(a -> log.info("put ledger type map into chain"))
                );
    }


}
