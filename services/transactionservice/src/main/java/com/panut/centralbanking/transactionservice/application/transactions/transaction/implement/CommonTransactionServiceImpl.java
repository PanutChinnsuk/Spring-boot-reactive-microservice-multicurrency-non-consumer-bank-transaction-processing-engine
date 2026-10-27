package com.panut.centralbanking.transactionservice.application.transactions.transaction.implement;

import com.panut.centralbanking.transactionservice.api.request.SameCurrencyTransferRequest;
import com.panut.centralbanking.transactionservice.application.ledgeraccount.LedgerCode;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.TransferPipeline;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.data.PipelineTransferData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.TransactionCreationPipelineResult;
import com.panut.centralbanking.transactionservice.application.transactions.transaction.service.CommonTransactionService;
import com.panut.centralbanking.transactionservice.core.TransactionType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@Transactional(propagation = Propagation.REQUIRED)
public class CommonTransactionServiceImpl implements CommonTransactionService {
    private final TransferPipeline transferPipeline;

    public CommonTransactionServiceImpl(TransferPipeline transferPipeline) {
        this.transferPipeline = transferPipeline;
    }


    private PipelineTransferData requestToData(final SameCurrencyTransferRequest r) {
        return new PipelineTransferData(r.getFromAccountId(), r.getToAccountId(), r.getAmount());
    }

    public Mono<TransactionCreationPipelineResult> sameCurrencyTransfer(final SameCurrencyTransferRequest r) {
        return transferPipeline
                .transfer(requestToData(r).withSingleLedgerCode(LedgerCode.COMMON_TRANSFER),
                        TransactionType.TRANSFER);
    }

    public Mono<TransactionCreationPipelineResult> sameCurrencyExpense(final SameCurrencyTransferRequest r) {
        return transferPipeline
                .transfer(requestToData(r).withSingleLedgerCode(LedgerCode.EXPENSE_TRANSFER),
                        TransactionType.ASSET_EXPENSE);
    }

    public Mono<TransactionCreationPipelineResult> sameCurrencyAsset(final SameCurrencyTransferRequest r) {
        return transferPipeline
                .transfer(requestToData(r).withSingleLedgerCode(LedgerCode.ASSET_TRANSFER),
                        TransactionType.ASSET_EXPENSE);
    }
}
