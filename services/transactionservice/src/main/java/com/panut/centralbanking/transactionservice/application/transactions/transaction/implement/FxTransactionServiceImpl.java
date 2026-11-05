package com.panut.centralbanking.transactionservice.application.transactions.transaction.implement;

import com.panut.centralbanking.transactionservice.api.request.BaseBuyForeignTransferRequest;
import com.panut.centralbanking.transactionservice.api.request.ForeignBuyBaseTransferRequest;
import com.panut.centralbanking.transactionservice.api.service.internalaccount.InternalAccountQueryServiceController;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.FxTransferPipeline;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.data.BaseBuyForeignTransferData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.data.ForeignBuyBaseTransferData;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.TransactionCreationPipelineResult;
import com.panut.centralbanking.transactionservice.application.transactions.transaction.service.FxTransactionService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
public class FxTransactionServiceImpl implements FxTransactionService {
    private final FxTransferPipeline fxTransferPipeline;
    private final InternalAccountQueryServiceController internalAccountService;

    public FxTransactionServiceImpl(FxTransferPipeline fxTransferPipeline,
                                    InternalAccountQueryServiceController internalAccountService) {
        this.fxTransferPipeline = fxTransferPipeline;
        this.internalAccountService = internalAccountService;
    }

    private ForeignBuyBaseTransferData foreignBuyBaseTransferData(final ForeignBuyBaseTransferRequest r) {
        return new ForeignBuyBaseTransferData(r.getFromAccountId(),
                r.getToAccountId(),
                r.getForeignAmount());
    }

    private BaseBuyForeignTransferData baseBuyForeignTransferData(final BaseBuyForeignTransferRequest r) {
        return new BaseBuyForeignTransferData(r.getFromAccountId(),
                r.getToAccountId(),
                r.getBaseAmount());
    }

    @Override
    public Mono<TransactionCreationPipelineResult> foreignBuyBase(final ForeignBuyBaseTransferRequest r) {
        return internalAccountService.getFxInternalAccounts()
                .map(internalAccounts -> {
                    return foreignBuyBaseTransferData(r)
                            .withInternalFxProfitAccountId(internalAccounts.getFxProfitInternalAccountId())
                            .withInternalFxClearingAccountId(internalAccounts.getFxClearingInternalAccountId());
                })
                .flatMap(fxTransferPipeline::foreignBuyBase);
    }

    @Override
    public Mono<TransactionCreationPipelineResult> baseBuyForeign(final BaseBuyForeignTransferRequest r) {
        return internalAccountService.getFxInternalAccounts()
                .map(internalAccounts -> {
                    return baseBuyForeignTransferData(r)
                            .withInternalCollectorAccountId(internalAccounts.getFxProfitInternalAccountId())
                            .withInternalClearingAccountId(internalAccounts.getFxClearingInternalAccountId());
                })
                .flatMap(fxTransferPipeline::baseBuyForeign);
    }
}
