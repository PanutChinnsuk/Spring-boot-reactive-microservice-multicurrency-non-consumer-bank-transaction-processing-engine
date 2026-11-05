package com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.repository;

import com.panut.centralbanking.transactionservice.application.transactions.line.query.TransactionLineQuery;
import com.panut.centralbanking.transactionservice.application.transactions.profile.query.TransactionProfileQuery;
import com.panut.centralbanking.transactionservice.application.transactions.transaction.query.TransactionQuery;
import com.panut.centralbanking.transactionservice.core.TransactionStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class TransactionManagePipelineRepository {
    private final TransactionQuery transactionQuery;
    private final TransactionProfileQuery transactionProfileQuery;
    private final TransactionLineQuery transactionLineQuery;

    public TransactionManagePipelineRepository(TransactionQuery transactionQuery,
                                               TransactionProfileQuery transactionProfileQuery,
                                               TransactionLineQuery transactionLineQuery) {
        this.transactionQuery = transactionQuery;
        this.transactionProfileQuery = transactionProfileQuery;
        this.transactionLineQuery = transactionLineQuery;
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
                );
    }


}
