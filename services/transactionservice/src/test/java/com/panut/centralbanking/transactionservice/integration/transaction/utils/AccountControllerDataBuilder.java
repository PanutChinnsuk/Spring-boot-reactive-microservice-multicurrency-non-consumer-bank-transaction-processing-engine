package com.panut.centralbanking.transactionservice.integration.transaction.utils;

import com.panut.centralbanking.transactionservice.api.service.account.AccountServiceController;
import com.panut.centralbanking.transactionservice.api.service.account.request.GetAccountsInfoForTransactionRequest;
import com.panut.centralbanking.transactionservice.api.service.account.response.GetAccountsInfoForTransactionResponse;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

public class AccountControllerDataBuilder {


    public static void controllerReturn(GetAccountsInfoForTransactionResponse accountsInfo,
                                             AccountServiceController accountServiceController) {
        doReturn(Mono.just(accountsInfo))
                .when(accountServiceController)
                .getAccountsInfoForTransaction(any(GetAccountsInfoForTransactionRequest.class));
    }
}
