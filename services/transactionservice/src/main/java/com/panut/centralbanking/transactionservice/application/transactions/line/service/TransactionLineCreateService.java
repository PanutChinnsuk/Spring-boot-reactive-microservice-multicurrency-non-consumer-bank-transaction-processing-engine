package com.panut.centralbanking.transactionservice.application.transactions.line.service;

import com.panut.centralbanking.transactionservice.application.transactions.line.TransactionLineRepositoryManager;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.create.data.TransactionLineCreateData;
import com.panut.centralbanking.transactionservice.infrastructure.model.Transaction;
import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionLine;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class TransactionLineCreateService {
    private final TransactionLineRepositoryManager manager;

    public TransactionLineCreateService(TransactionLineRepositoryManager manager) {
        this.manager = manager;
    }


    public Mono<List<TransactionLine>> createLinesV1(final Transaction transaction,
                                                     final List<TransactionLineCreateData> datas) {
        return manager.saveAll(datas.stream().map(data ->
                TransactionLine.builder()
                        .identity(data.getIdentity())
                        .accountId(data.getAccountId())
                        .transactionId(transaction.getId())
                        .basedAmount(data.getBasedAmount())
                        .convertedAmount(data.getConvertedAmount())
                        .direction(data.getDirection())
                        .ledgerAccountId(data.getLedgerAccountId())
                        .build()).toList());
    }
}
