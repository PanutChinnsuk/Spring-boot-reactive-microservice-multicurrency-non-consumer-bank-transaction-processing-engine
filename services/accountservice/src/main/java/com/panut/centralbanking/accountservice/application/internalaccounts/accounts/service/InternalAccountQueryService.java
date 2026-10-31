package com.panut.centralbanking.accountservice.application.internalaccounts.accounts.service;

import com.panut.centralbanking.accountservice.api.response.serviceonly.GetFxInternalAccountResponse;
import reactor.core.publisher.Mono;

public interface InternalAccountQueryService {
    Mono<GetFxInternalAccountResponse> getFxInternalAccount();

}
