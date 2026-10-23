package com.panut.centralbanking.accountservice.application.account.service;

import com.panut.centralbanking.accountservice.api.request.serviceonly.GetAccountsInfoForTransactionRequest;
import com.panut.centralbanking.accountservice.api.response.serviceonly.GetAccountsInfoForTransactionResponse;
import com.panut.centralbanking.appcore.common.response.CommonResult;
import reactor.core.publisher.Mono;

public interface AccountInfoService {
    Mono<CommonResult<GetAccountsInfoForTransactionResponse>>
    getAccountInfosForTransaction(GetAccountsInfoForTransactionRequest r);
}
