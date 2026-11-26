package com.panut.centralbanking.transactionservice.application.transactions.contents.multifxdirection.service;


import com.panut.centralbanking.transactionservice.application.transactions.contents.multifxdirection.TransactionMultiFxDirectionContentRepositoryManager;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.TransactionMultiFxContext;
import com.panut.centralbanking.transactionservice.infrastructure.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class TransactionMultiFxDirectionContentService {
    private final TransactionMultiFxDirectionContentRepositoryManager manager;

    public TransactionMultiFxDirectionContentService(TransactionMultiFxDirectionContentRepositoryManager manager) {
        this.manager = manager;
    }



    public Mono<Transaction> trackDirections(final TransactionMultiFxContext context,
                                             final Transaction transaction) {
        return Flux.defer(() -> {
          if (!context.hasDirectionContents()) {
              log.info("transaction has multi fx context but no direction contents");
              return Flux.empty();
          }

          log.info("transaction has multi fx direction context, size: {}",
                    context.getDirectionContents().size());

          var directions = context.getDirectionContents()
                  .stream()
                  .map(ctn ->
                          ctn.withTransactionId(transaction.getId()))
                  .toList();

          return manager.saveAll(directions)
                  .doOnNext(a ->
                          log.info("track multi fx direction: {}",
                                  a.getFxDirection()));
        }).then(Mono.just(transaction));
    }
}
