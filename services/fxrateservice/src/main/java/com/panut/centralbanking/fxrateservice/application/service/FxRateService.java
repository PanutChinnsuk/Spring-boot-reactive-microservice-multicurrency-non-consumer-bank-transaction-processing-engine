package com.panut.centralbanking.fxrateservice.application.service;

import com.panut.centralbanking.appcore.common.response.CommonResult;
import com.panut.centralbanking.fxrateservice.api.request.FxRateCreateRequest;
import com.panut.centralbanking.fxrateservice.api.request.FxRateGetLatestRequest;
import com.panut.centralbanking.fxrateservice.api.response.FxRateCreatedResponse;
import com.panut.centralbanking.fxrateservice.api.response.FxRateGetLatestResponse;
import reactor.core.publisher.Mono;

public interface FxRateService {
    Mono<CommonResult<FxRateCreatedResponse>> createFxRate(FxRateCreateRequest r);
    Mono<FxRateGetLatestResponse> getLatest(FxRateGetLatestRequest r);
}
