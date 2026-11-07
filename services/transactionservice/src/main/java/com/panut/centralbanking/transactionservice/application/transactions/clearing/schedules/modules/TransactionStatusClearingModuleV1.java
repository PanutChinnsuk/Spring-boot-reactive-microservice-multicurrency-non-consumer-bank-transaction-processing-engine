package com.panut.centralbanking.transactionservice.application.transactions.clearing.schedules.modules;

import com.panut.centralbanking.transactionservice.application.property.TransactionClearingProperty;
import com.panut.centralbanking.transactionservice.core.BalanceChangeEventStatus;
import com.panut.centralbanking.transactionservice.core.TransactionStatus;
import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class TransactionStatusClearingModuleV1 {
    private final TransactionRepository transactionRepository;
    private final TransactionClearingProperty property;


    public TransactionStatusClearingModuleV1(TransactionRepository transactionRepository,
                                              TransactionClearingProperty property) {
        this.transactionRepository = transactionRepository;
        this.property = property;
    }

    private final BalanceChangeEventStatus EXPECTING_NO_EVENTS_WITH_STATUS =
            BalanceChangeEventStatus.PENDING;


    private Disposable update(final TransactionStatus upNextStatus,
                              final TransactionStatus currentStatus,
                              final List<String> transactionIds) {
        return Mono.defer(() -> {
                    if (transactionIds == null || transactionIds.isEmpty())
                        return Mono.empty();


                    return transactionRepository.toNextStatus(upNextStatus, currentStatus,
                            transactionIds);
                })
                .doOnNext(a -> log.info("Cleared {} transactions", currentStatus))
                .subscribe();
    }

    public Mono<Void> clear(final TransactionStatus pending,
                            final String exludedIdFlag) {
        log.info("Clearing {} transactions", pending);

        return transactionRepository
                .getClearedTransactions(pending, EXPECTING_NO_EVENTS_WITH_STATUS,
                        property.getSizeLimit(), exludedIdFlag)
                .collectList()
                .doOnNext(a -> log.info("TRANSACTION IDS CLEARED LIST SIZE {}",
                        a.size()))
                .map(ids -> update(pending.getUpnextStatus(), pending, ids))
                .then();


    }
}
