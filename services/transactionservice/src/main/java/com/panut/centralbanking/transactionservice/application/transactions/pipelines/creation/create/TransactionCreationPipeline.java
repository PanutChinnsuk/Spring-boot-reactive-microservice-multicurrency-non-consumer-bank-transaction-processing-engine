package com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.create;

import com.panut.centralbanking.transactionservice.application.transactions.contents.multifx.service.TransactionMultiFxContentService;
import com.panut.centralbanking.transactionservice.application.transactions.contents.multifxdirection.service.TransactionMultiFxDirectionContentService;
import com.panut.centralbanking.transactionservice.application.transactions.line.service.TransactionLineCreateService;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.TransactionCreationPipelineResult;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.post.PostTransactionRegisterData;
import com.panut.centralbanking.transactionservice.application.transactions.profile.service.TransactionProfileCreateService;
import com.panut.centralbanking.transactionservice.application.transactions.transaction.service.TransactionCreateService;
import com.panut.centralbanking.transactionservice.infrastructure.model.datacontent.TransactionMultiFxDirectionContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@Transactional(propagation = Propagation.REQUIRED)
public class TransactionCreationPipeline {
    private final TransactionCreateService transactionCreateService;
    private final TransactionProfileCreateService transactionProfileCreateService;
    private final TransactionLineCreateService transactionLineCreateService;
    private final TransactionMultiFxContentService multiFxContentService;
    private final TransactionMultiFxDirectionContentService multiFxDirectionContentService;


    public TransactionCreationPipeline(TransactionCreateService transactionCreateService,
                                       TransactionProfileCreateService transactionProfileCreateService,
                                       TransactionLineCreateService transactionLineCreateService,
                                       TransactionMultiFxContentService multiFxContentService,
                                       TransactionMultiFxDirectionContentService multiFxDirectionContentService) {
        this.transactionCreateService = transactionCreateService;
        this.transactionProfileCreateService = transactionProfileCreateService;
        this.transactionLineCreateService = transactionLineCreateService;
        this.multiFxContentService = multiFxContentService;
        this.multiFxDirectionContentService = multiFxDirectionContentService;
    }


    public Mono<TransactionCreationPipelineResult> create(final PostTransactionRegisterData data) {
        return transactionCreateService.createTransaction(data.getTransactionType())
                .doOnNext(a -> log.info("Created transaction model."))
                .flatMap(transaction -> new CreationPipelineChain().transaction(transaction))
                .doOnNext(a -> log.info("Transaction model to CreationPipelineChain."))
                .flatMap(chain -> transactionProfileCreateService
                        .createProfile(chain.getTransaction().getId(),
                                data.getFxDirection(),
                                data.getFxRateId(),
                                data.getPossbileTransactionReversalContext())
                        .flatMap(profile -> chain.transactionProfile(profile))
                        .doOnNext(a -> log.info("Transaction profile to chain."))
                )
                .doOnNext(a -> log.info("Created transaction profile."))
                .flatMap(chain -> transactionLineCreateService
                        .createLinesV1(chain.getTransaction(),
                                data.getLineDatas())
                        .flatMap(lines -> chain.transactionLines(lines))
                        .doOnNext(a -> log.info("Transaction lines to chain."))
                )
                .flatMap(chain -> {
                    if (data.getTransactionMultiFxContext() == null)
                        return Mono.just(chain);

                    var context = data.getTransactionMultiFxContext();
                    var transaction = chain.getTransaction();
                    log.info("Transaction has TransactionMultiFxContext");
                    return multiFxContentService
                            .include(context, transaction)
                            .then(multiFxDirectionContentService
                                    .trackDirections(context, transaction))
                            .thenReturn(chain);
                })
                .doOnNext(a -> log.info("Created transaction lines."))
                .map(chain -> new TransactionCreationPipelineResult()
                        .transaction(chain.getTransaction())
                        .transactionProfile(chain.getTransactionProfile())
                        .transactionLines(chain.getTransactionLines())
                );
    }
}
