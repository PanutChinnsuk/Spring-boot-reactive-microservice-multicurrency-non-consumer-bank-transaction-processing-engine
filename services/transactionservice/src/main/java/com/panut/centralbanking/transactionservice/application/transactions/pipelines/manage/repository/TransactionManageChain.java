package com.panut.centralbanking.transactionservice.application.transactions.pipelines.manage.repository;

import com.panut.centralbanking.appcore.exceptions.NotFound;
import com.panut.centralbanking.appcore.exceptions.SystemMisused;
import com.panut.centralbanking.transactionservice.api.service.account.data.BalanceChangeData;
import com.panut.centralbanking.transactionservice.api.service.account.request.PerTransactionBalanceChangeRequest;
import com.panut.centralbanking.transactionservice.core.LedgerType;
import com.panut.centralbanking.transactionservice.infrastructure.model.Transaction;
import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionLine;
import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionProfile;
import com.panut.centralbanking.transactionservice.infrastructure.model.events.BalanceChangeEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
@Slf4j
public class TransactionManageChain {
    private Transaction transaction;
    private TransactionProfile transactionProfile;
    private Flux<TransactionLine> transactionLines;
    private Map<String, LedgerType> ledgerTypeMap;
    private List<BalanceChangeEvent> balanceChangeEvents = new ArrayList<>();


    public LedgerType getLedgerTypeOfLine(final TransactionLine line) {
        if (ledgerTypeMap == null)
            throw new SystemMisused("ledgerTypeMap is null due to calling getLedgerTypeOfLine " +
                    "before setting ledgerTypeMap");

        LedgerType ledgerType = ledgerTypeMap.get(line.getLedgerAccountId());
        if (ledgerTypeMap == null)
            throw new NotFound("Ledger type not found for transaction line id of " +
                    line.getId());

        return ledgerType;
    }

    public Mono<TransactionManageChain> transaction(final Transaction transaction) {
        return Mono.defer(() -> {
            this.transaction = transaction;

            return Mono.just(this);
        });
    }

    public Mono<TransactionManageChain> transactionProfile(final TransactionProfile transactionProfile) {
        return Mono.defer(() -> {
            this.transactionProfile = transactionProfile;

            return Mono.just(this);
        });
    }

    public Mono<TransactionManageChain> transactionLines(final Flux<TransactionLine> transactionLines ) {
        return Mono.defer(() -> {
            this.transactionLines = transactionLines;


            return Mono.just(this);
        });
    }

    public Mono<TransactionManageChain> ledgerTypeMap(final Map<String, LedgerType> ledgerTypeMap) {
        return Mono.defer(() -> {
            this.ledgerTypeMap = ledgerTypeMap;

            return Mono.just(this);
        });
    }

    public void addBalanceChangeEvent(final BalanceChangeEvent event) {
        balanceChangeEvents.add(event);
        log.info("added balance change event");
    }

    public PerTransactionBalanceChangeRequest toPerTransactionBalanceChangeRequest() {
        if (balanceChangeEvents.isEmpty())
            throw new SystemMisused("balanceChangeEvents list can't convert to " +
                    "toPerTransactionBalanceChangeRequest, due to empty list.");

        return new PerTransactionBalanceChangeRequest(
                balanceChangeEvents.stream()
                        .map(BalanceChangeData::eventToRequest)
                        .toList()
        );
    }
}
