package com.panut.centralbanking.transactionservice.api.service.fxRate;

import com.panut.centralbanking.appcore.client.Retrieves;
import com.panut.centralbanking.appcore.common.response.CommonResult;
import com.panut.centralbanking.appcore.exceptions.NotFound;
import com.panut.centralbanking.appcore.servicemaps.map.FxRateService;
import com.panut.centralbanking.transactionservice.api.service.fxRate.request.FxRateGetByIdRequest;
import com.panut.centralbanking.transactionservice.api.service.fxRate.request.FxRateGetLatestRequest;
import com.panut.centralbanking.transactionservice.api.service.fxRate.request.FxRateMultiGetLatestRequest;
import com.panut.centralbanking.transactionservice.api.service.fxRate.response.FxRateGetResponse;
import com.panut.centralbanking.transactionservice.api.service.fxRate.response.FxRateMultiGetResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class FxRateServiceController {
    private final WebClient fxRateServiceWebClient;

    public FxRateServiceController(WebClient fxRateServiceWebClient) {
        this.fxRateServiceWebClient = fxRateServiceWebClient;
    }

    ParameterizedTypeReference<CommonResult<FxRateGetResponse>> getFxRateResponseType
            = new ParameterizedTypeReference<CommonResult<FxRateGetResponse>>() {};

    ParameterizedTypeReference<CommonResult<FxRateMultiGetResponse>> multiGetFxRateResponseType
            = new ParameterizedTypeReference<CommonResult<FxRateMultiGetResponse>>() {};

    private final String LOG_NAME = "FxRateService ";

    public Mono<FxRateGetResponse> getLatestFxRate(final FxRateGetLatestRequest r) {
        return Retrieves
                .doRequest(fxRateServiceWebClient,
                        FxRateService.GET_LATEST_FULL_PATH,
                        r,
                        NotFound.class,
                        LOG_NAME + "getLatestFxRate",
                        getFxRateResponseType)
                .map(result -> result.getOptionalBody());
    }

    public Mono<FxRateMultiGetResponse> multiGetLatestFxRate(final FxRateMultiGetLatestRequest r) {
        return Retrieves
                .doRequest(fxRateServiceWebClient,
                        FxRateService.MULTI_GET_LATEST_FULL_PATH,
                        r,
                        NotFound.class,
                        LOG_NAME + "multiGetLatestFxRate",
                        multiGetFxRateResponseType)
                .map(result -> result.getOptionalBody());
    }


    public Mono<FxRateGetResponse> getById(final FxRateGetByIdRequest r) {
        return Retrieves
                .doRequest(fxRateServiceWebClient,
                        FxRateService.GET_BY_ID_FULL_PATH,
                        r,
                        NotFound.class,
                        LOG_NAME + "getLatestFxRate",
                        getFxRateResponseType)
                .map(result -> result.getOptionalBody());
    }
}
