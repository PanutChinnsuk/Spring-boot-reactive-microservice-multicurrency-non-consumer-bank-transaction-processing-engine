package com.panut.centralbanking.accountservice.application.accounts.account.service;

import com.panut.centralbanking.accountservice.api.request.serviceonly.transactionprimary.GetAccountsInfoForTransactionRequest;
import com.panut.centralbanking.accountservice.api.response.serviceonly.GetAccountsInfoForTransactionResponse;
import com.panut.centralbanking.appcore.common.response.CommonResult;
import reactor.core.publisher.Mono;

public interface AccountInfoService {
    Mono<CommonResult<GetAccountsInfoForTransactionResponse>>
    getAccountInfosForTransaction(GetAccountsInfoForTransactionRequest r);
}
