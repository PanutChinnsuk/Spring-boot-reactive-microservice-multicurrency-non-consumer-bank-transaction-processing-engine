package com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data;

import com.panut.centralbanking.transactionservice.infrastructure.model.datacontent.TransactionMultiFxContent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransactionMultiFxContext {
    private List<TransactionMultiFxContent> contents;
}
