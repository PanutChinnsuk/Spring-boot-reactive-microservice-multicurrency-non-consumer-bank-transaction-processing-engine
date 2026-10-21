package com.panut.centralbanking.transactionservice.application.ledgeraccount.service;

import com.panut.centralbanking.appcore.common.response.CommonResult;
import com.panut.centralbanking.transactionservice.api.request.LedgerAccountCreateRequest;
import reactor.core.publisher.Mono;

public interface LedgerAccountCreateService {
    Mono<CommonResult> createLedgerAccount(LedgerAccountCreateRequest r);
}
