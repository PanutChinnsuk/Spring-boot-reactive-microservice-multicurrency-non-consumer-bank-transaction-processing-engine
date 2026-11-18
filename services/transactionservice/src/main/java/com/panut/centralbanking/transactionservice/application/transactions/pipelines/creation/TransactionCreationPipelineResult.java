package com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.panut.centralbanking.transactionservice.core.FxDirection;
import com.panut.centralbanking.transactionservice.core.TransactionStatus;
import com.panut.centralbanking.transactionservice.core.TransactionType;
import com.panut.centralbanking.transactionservice.infrastructure.model.Transaction;
import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionLine;
import com.panut.centralbanking.transactionservice.infrastructure.model.TransactionProfile;
import lombok.Data;
import reactor.core.publisher.Mono;

import java.util.List;

@Data
public class TransactionCreationPipelineResult {
    private String transactionId;
    private TransactionType transactionType;
    private TransactionStatus transactionStatus;

    private FxDirection fxDirection;
    private boolean isReversed;

    private int transactionLines;
    private List<String> transactionLineIds;

    @JsonIgnore
    public TransactionCreationPipelineResult transaction(final Transaction transaction) {
        this.transactionId = transaction.getId();
        this.transactionType = transaction.getTransactionType();
        this.transactionStatus = transaction.getTransactionStatus();

        return this;
    }

    @JsonIgnore
    public TransactionCreationPipelineResult transactionProfile(final TransactionProfile transactionProfile) {
        this.fxDirection = transactionProfile.getFxDirection();
        this.isReversed = transactionProfile.isReversedState();

        return this;
    }

    @JsonIgnore
    public TransactionCreationPipelineResult transactionLines(final List<TransactionLine> transactionLines) {
        this.transactionLines = transactionLines.size();
        this.transactionLineIds = transactionLines
                .stream().
                map(TransactionLine::getId)
                .toList();


        return this;
    }
}
