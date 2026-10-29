package com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.repository;

import com.panut.centralbanking.transactionservice.api.service.account.AccountServiceController;
import com.panut.centralbanking.transactionservice.api.service.account.request.GetAccountsInfoForTransactionRequest;
import com.panut.centralbanking.transactionservice.api.service.account.response.GetAccountsInfoForTransactionResponse;
import com.panut.centralbanking.transactionservice.application.ledgeraccount.query.LedgerAccountQuery;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.data.AbstractPipelineTransferData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class BusinessPipelineRepository {
    private final AccountServiceController accountServiceController;
    private final LedgerAccountQuery ledgerAccountQuery;

    public BusinessPipelineRepository(AccountServiceController accountServiceController,
                            LedgerAccountQuery ledgerAccountQuery) {
        this.accountServiceController = accountServiceController;
        this.ledgerAccountQuery = ledgerAccountQuery;
    }



    public Mono<GetAccountsInfoForTransactionResponse> getAccountsInfoForTransaction(
            final AbstractPipelineTransferData data) {
        return data.validate()
                .thenReturn(data.asAccountIds())
                .doOnNext(a -> log.info("Returned account ids used in the transfer pipeline" +
                        " after validated AbstractPipelineTransferData"))
                .flatMap(accountIds -> accountServiceController
                        .getAccountsInfoForTransaction(new GetAccountsInfoForTransactionRequest(
                                accountIds))
                )
                .doOnNext(a -> log.info(
                        "Returned GetAccountsInfoForTransactionResponse from calling account service."
                ));
    }



}
