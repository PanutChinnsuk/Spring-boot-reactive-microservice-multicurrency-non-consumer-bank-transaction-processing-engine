package com.panut.centralbanking.transactionservice.application.transactions.line.service;

import com.panut.centralbanking.transactionservice.application.transactions.line.TransactionLineRepositoryManager;
import com.panut.centralbanking.transactionservice.application.transactions.pipeline.data.create.TransactionLineCreateData;
import com.panut.centralbanking.transactionservice.core.TransactionLineDirection;
import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionLine;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionLineCreateService {
    private final TransactionLineRepositoryManager manager;

    public TransactionLineCreateService(TransactionLineRepositoryManager manager) {
        this.manager = manager;
    }


    public Mono<List<TransactionLine>> createLinesV1(final String transactionId,
                                                     final List<TransactionLineCreateData> datas) {
        return manager.saveAll(datas.stream().map(data ->
                TransactionLine.builder()
                        .accountId(data.getAccountId())
                        .transactionId(transactionId)
                        .basedAmount(data.getBasedAmount())
                        .convertedAmount(data.getConvertedAmount())
                        .direction(data.getDirection())
                        .ledgerAccountId(data.getLedgerAccountId())
                        .build()).toList());
    }
}
