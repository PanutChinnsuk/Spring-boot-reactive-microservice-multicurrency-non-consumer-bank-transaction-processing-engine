package com.panut.centralbanking.transactionservice.api.service.fxRate;

import com.panut.centralbanking.appcore.client.Retrieves;
import com.panut.centralbanking.appcore.common.response.CommonResult;
import com.panut.centralbanking.appcore.exceptions.NotFound;
import com.panut.centralbanking.appcore.servicemaps.map.FxRateService;
import com.panut.centralbanking.transactionservice.api.service.fxRate.request.FxRateGetLatestRequest;
import com.panut.centralbanking.transactionservice.api.service.fxRate.response.FxRateGetLatestResponse;
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

    ParameterizedTypeReference<CommonResult<FxRateGetLatestResponse>> getLatestFxRateResponseType
            = new ParameterizedTypeReference<CommonResult<FxRateGetLatestResponse>>() {};

    private final String LOG_NAME = "FxRateService ";

    public Mono<FxRateGetLatestResponse> getLatestFxRate(final FxRateGetLatestRequest r) {
        return Retrieves
                .doRequest(fxRateServiceWebClient,
                        FxRateService.GET_LATEST_FULL_PATH,
                        r,
                        NotFound.class,
                        LOG_NAME + "getLatestFxRate",
                        getLatestFxRateResponseType)
                .map(result -> result.getOptionalBody());
    }
}
