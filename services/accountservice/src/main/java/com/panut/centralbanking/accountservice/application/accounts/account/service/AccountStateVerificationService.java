package com.panut.centralbanking.accountservice.application.accounts.account.service;

import com.panut.centralbanking.accountservice.api.request.serviceonly.VerifyAccountsStateRequest;
import com.panut.centralbanking.appcore.common.response.CommonResult;
import reactor.core.publisher.Mono;

public interface AccountStateVerificationService {
    Mono<CommonResult> verifyAccountsState(VerifyAccountsStateRequest r);
}
