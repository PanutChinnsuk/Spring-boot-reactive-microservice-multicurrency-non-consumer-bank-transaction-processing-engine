package com.panut.centralbanking.accountservice.application.accounts.accountoverdraft.service;

import com.panut.centralbanking.accountservice.api.data.AccountOverdraftCreateData;
import com.panut.centralbanking.appcore.common.response.CommonResult;
import reactor.core.publisher.Mono;

public interface AccountOverdraftService {
    Mono<CommonResult> overdraft(AccountOverdraftCreateData data);
}
