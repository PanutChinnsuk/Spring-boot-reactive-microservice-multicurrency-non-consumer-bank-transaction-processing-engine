package com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.v1;

import com.panut.centralbanking.transactionservice.api.service.account.AccountServiceController;
import com.panut.centralbanking.transactionservice.api.service.fxRate.FxRateServiceController;
import com.panut.centralbanking.transactionservice.application.ledgeraccount.query.LedgerAccountQuery;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.repository.BusinessPipelineRepository;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.TransactionRegisterPipeline;
import org.springframework.stereotype.Component;

@Component
public class InternalTransferPipelineModuleV1 {
    private final BusinessPipelineRepository repository;
    private final TransactionRegisterPipeline transactionRegisterPipeline;
    private final AccountServiceController accountServiceController;
    private final LedgerAccountQuery ledgerAccountQuery;


    public InternalTransferPipelineModuleV1(BusinessPipelineRepository repository,
                                      TransactionRegisterPipeline transactionRegisterPipeline,
                                      AccountServiceController accountServiceController,
                                      LedgerAccountQuery ledgerAccountQuery) {
        this.repository = repository;
        this.transactionRegisterPipeline = transactionRegisterPipeline;
        this.accountServiceController = accountServiceController;
        this.ledgerAccountQuery = ledgerAccountQuery;
    }
}
