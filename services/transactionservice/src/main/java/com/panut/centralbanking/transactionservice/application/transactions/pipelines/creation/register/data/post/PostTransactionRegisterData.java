package com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.post;

import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.create.data.TransactionLineCreateData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.TransactionReversalContext;
import com.panut.centralbanking.transactionservice.core.FxDirection;
import com.panut.centralbanking.transactionservice.core.TransactionType;
import jakarta.annotation.Nullable;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class PostTransactionRegisterData {
    private final TransactionType transactionType;
    private final List<TransactionLineCreateData> lineDatas;
    private final String fxRateId;
    private final FxDirection fxDirection;
    private final Set<String> usingAccountIds;

    @Nullable
    private final TransactionReversalContext possbileTransactionReversalContext;





}
