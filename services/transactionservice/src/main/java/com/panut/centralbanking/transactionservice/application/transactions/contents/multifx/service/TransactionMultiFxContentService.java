package com.panut.centralbanking.transactionservice.application.transactions.contents.multifx.service;

import com.panut.centralbanking.transactionservice.application.transactions.contents.multifx.TransactionMultiFxContentRepositoryManager;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.TransactionMultiFxContext;
import com.panut.centralbanking.transactionservice.infrastructure.model.Transaction;
import com.panut.centralbanking.transactionservice.infrastructure.model.datacontent.TransactionMultiFxContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class TransactionMultiFxContentService {
    private final TransactionMultiFxContentRepositoryManager manager;

    public TransactionMultiFxContentService(TransactionMultiFxContentRepositoryManager manager) {
        this.manager = manager;
    }



    public Mono<Transaction> include(final TransactionMultiFxContext context,
                                     final Transaction transaction) {
        return Flux.defer(() -> {
            if (!context.hasContents()) {
                log.info("transaction has multi fx context but no contents");
                return Flux.empty();
            }

            log.info("transaction has multi fx context, size: {}", context.getContents().size());

            return manager.saveAll(context.getContents()
                            .stream()
                            .map(ctn ->
                                    ctn.withTransactionId(transaction.getId()))
                            .toList()
                    )
                    .doOnNext(a ->
                            log.info("Included multi fx content, fx rate id, {}",
                                    a.getFxRateId()));

        }).then(Mono.just(transaction));
    }
}
