package com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.validate;

import com.panut.centralbanking.appcore.exceptions.SystemMisused;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.TransactionCreationPipelineResult;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.create.TransactionCreationPipeline;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.create.data.TransactionLineCreateData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.post.PostTransactionRegisterData;
import com.panut.centralbanking.transactionservice.core.TransactionLineDirection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Component
@Slf4j
@Transactional(propagation = Propagation.REQUIRED)
public class TransactionValidationPipeline {
    private final TransactionCreationPipeline transactionCreationPipeline;

    public TransactionValidationPipeline(TransactionCreationPipeline transactionCreationPipeline) {
        this.transactionCreationPipeline = transactionCreationPipeline;
    }

    public Mono<Void> validateTransactionLedger(final List<TransactionLineCreateData> lineDatas) {
        return Mono.defer(() -> {
            AtomicReference<BigDecimal> totalCredit = new AtomicReference<>(BigDecimal.ZERO);
            AtomicReference<BigDecimal> totalDebit = new AtomicReference<>(BigDecimal.ZERO);

            lineDatas.stream().forEach(data -> {
                TransactionLineDirection direction = data.getDirection();
                BigDecimal convertedAmount = data.getConvertedAmount();

                if (direction.equals(TransactionLineDirection.CREDIT))
                    totalCredit.set(totalCredit.get().add(convertedAmount));
                if (direction.equals(TransactionLineDirection.DEBIT))
                    totalDebit.set(totalDebit.get().add(convertedAmount));
            });

            if (!totalCredit.get().equals(totalDebit.get())) {
                return Mono.error(() -> new SystemMisused("Unbalanced transaction ledger."));
            }

            return Mono.empty();
        });
    }

    public Mono<Void> validateTransactionEntries(final PostTransactionRegisterData post) {
        return Mono.defer(() -> {
            if (post.getLineDatas().size() <= 1)
                return Mono.error(() -> new SystemMisused("A transaction's lines must have atleast" +
                        " 2 sides of entry."));
            if (post.getUsingAccountIds().size() == 1)
                return Mono.error(() -> new SystemMisused("Atleast two accounts must be used in a " +
                        "transaction."));



            return Mono.empty();
        });
    }

    public Mono<TransactionCreationPipelineResult> validate(final PostTransactionRegisterData data) {
        return validateTransactionEntries(data)
                .then(validateTransactionLedger(data.getLineDatas()))
                .doOnNext(a -> log.info("Validated transaction lines create data."))
                .then(transactionCreationPipeline.create(data));
    }
}
