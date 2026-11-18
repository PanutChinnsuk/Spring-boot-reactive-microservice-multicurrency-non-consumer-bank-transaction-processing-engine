package com.panut.centralbanking.transactionservice.application.transactions.transaction.implement;

import com.panut.centralbanking.transactionservice.api.request.InternalLinearTransferRequest;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.InternalTransferPipeline;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.data.InternalLinearTransferPipelineData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.TransactionCreationPipelineResult;
import com.panut.centralbanking.transactionservice.application.transactions.transaction.service.InternalLinearTransactionService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class InternalLinearTransactionServiceImpl implements InternalLinearTransactionService {
    private final InternalTransferPipeline internalTransferPipeline;

    public InternalLinearTransactionServiceImpl(InternalTransferPipeline internalTransferPipeline) {
        this.internalTransferPipeline = internalTransferPipeline;
    }


    @Override
    public Mono<TransactionCreationPipelineResult> linear(final InternalLinearTransferRequest r) {
        return internalTransferPipeline.customLinearTransfer(
                new InternalLinearTransferPipelineData(r.getCustomDatas(),
                        r.getTransferPolicies(),
                        r.getLedgerPolicies())
        );
    }
}
