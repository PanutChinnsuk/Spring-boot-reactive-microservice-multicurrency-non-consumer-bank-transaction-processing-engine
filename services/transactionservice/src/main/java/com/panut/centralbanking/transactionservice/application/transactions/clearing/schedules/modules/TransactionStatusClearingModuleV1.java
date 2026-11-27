package com.panut.centralbanking.transactionservice.application.transactions.clearing.schedules.modules;

import com.panut.centralbanking.transactionservice.application.property.TransactionClearingProperty;
import com.panut.centralbanking.transactionservice.core.BalanceChangeEventStatus;
import com.panut.centralbanking.transactionservice.core.TransactionStatus;
import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionProfileRepository;
import com.panut.centralbanking.transactionservice.infrastructure.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class TransactionStatusClearingModuleV1 {
    private final TransactionRepository transactionRepository;
    private final TransactionProfileRepository transactionProfileRepository;
    private final TransactionClearingProperty property;


    public TransactionStatusClearingModuleV1(TransactionRepository transactionRepository,
                                              TransactionProfileRepository transactionProfileRepository,
                                              TransactionClearingProperty property) {
        this.transactionRepository = transactionRepository;
        this.transactionProfileRepository = transactionProfileRepository;
        this.property = property;
    }

    private final BalanceChangeEventStatus EXPECTING_NO_EVENTS_WITH_STATUS =
            BalanceChangeEventStatus.PENDING;


    private Mono<Void> update(final TransactionStatus upNextStatus,
                              final TransactionStatus currentStatus,
                              final List<String> transactionIds) {
        return Mono.defer(() -> {
                    if (transactionIds == null || transactionIds.isEmpty())
                        return Mono.empty();


                    return transactionRepository.toNextStatus(upNextStatus, currentStatus,
                            transactionIds);
                })
                .doOnNext(a -> log.info("Cleared {} transactions", currentStatus))
                .then();
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
                .flatMap(ids ->
                        update(pending.getUpnextStatus(), pending, ids)
                                .thenReturn(ids)
                )
                .flatMap(ids -> {
                    var upNextStatus = pending.getUpnextStatus();
                    log.info("checking if upNextStatus required further action, {}",
                            upNextStatus);
                    if (upNextStatus.equals(TransactionStatus.POSTED) && !ids.isEmpty()) {
                        log.info("upNextStatus is POSTED updating posted timestamp for " +
                                "transaction profile");
                        return transactionProfileRepository.setPosted(ids,
                                LocalDateTime.now());
                    }


                    return Mono.empty();
                })

                .then();


    }
}
