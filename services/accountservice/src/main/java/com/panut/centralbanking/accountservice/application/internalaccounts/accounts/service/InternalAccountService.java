package com.panut.centralbanking.accountservice.application.internalaccounts.accounts.service;

import com.panut.centralbanking.accountservice.api.request.useronly.InternalAccountAddRequest;
import com.panut.centralbanking.accountservice.api.request.useronly.InternalAccountIdRequest;
import com.panut.centralbanking.accountservice.core.InternalAccountType;
import com.panut.centralbanking.accountservice.core.InternalBranch;
import com.panut.centralbanking.appcore.common.response.CommonResult;
import reactor.core.publisher.Mono;

public interface InternalAccountService {

    Mono<CommonResult> add(String accountId,
                           InternalBranch branch,
                           InternalAccountType internalAccountType);
    Mono<CommonResult> hardDelete(InternalAccountIdRequest r);
}
