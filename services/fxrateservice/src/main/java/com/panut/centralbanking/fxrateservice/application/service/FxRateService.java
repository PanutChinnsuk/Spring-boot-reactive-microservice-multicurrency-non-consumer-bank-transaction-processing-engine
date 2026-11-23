package com.panut.centralbanking.fxrateservice.application.service;

import com.panut.centralbanking.appcore.common.response.CommonResult;
import com.panut.centralbanking.fxrateservice.api.request.FxRateCreateRequest;
import com.panut.centralbanking.fxrateservice.api.request.FxRateGetByIdRequest;
import com.panut.centralbanking.fxrateservice.api.request.FxRateGetLatestRequest;
import com.panut.centralbanking.fxrateservice.api.request.FxRateMultiGetLatestRequest;
import com.panut.centralbanking.fxrateservice.api.response.FxRateCreatedResponse;
import com.panut.centralbanking.fxrateservice.api.response.FxRateGetResponse;
import com.panut.centralbanking.fxrateservice.api.response.FxRateMultiGetResponse;
import reactor.core.publisher.Mono;

public interface FxRateService {
    Mono<CommonResult<FxRateCreatedResponse>> createFxRate(FxRateCreateRequest r);
    Mono<CommonResult<FxRateGetResponse>> getLatest(FxRateGetLatestRequest r);
    Mono<CommonResult<FxRateMultiGetResponse>> multiGetLatest(FxRateMultiGetLatestRequest r);

    Mono<CommonResult<FxRateGetResponse>> getById(FxRateGetByIdRequest r);

}
