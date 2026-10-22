package com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.post;

import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.create.data.create.TransactionLineCreateData;
import lombok.Data;

import java.util.List;

@Data
public class PostTransactionRegisterData {
    private final List<TransactionLineCreateData> lineDatas;

}
