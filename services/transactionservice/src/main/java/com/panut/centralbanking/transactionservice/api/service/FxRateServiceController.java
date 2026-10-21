package com.panut.centralbanking.transactionservice.api.service;

import com.panut.centralbanking.appcore.client.Retrieves;
import com.panut.centralbanking.appcore.exceptions.NotFound;
import com.panut.centralbanking.appcore.servicemaps.map.FxRateService;
import com.panut.centralbanking.transactionservice.api.service.request.FxRateGetLatestRequest;
import com.panut.centralbanking.transactionservice.api.service.response.FxRateGetLatestResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class FxRateServiceController {
    private final WebClient fxRateServiceWebClient;

    public FxRateServiceController(WebClient fxRateServiceWebClient) {
        this.fxRateServiceWebClient = fxRateServiceWebClient;
    }

    private final String LOG_NAME = "FxRateService";

    public Mono<FxRateGetLatestResponse> getLatestFxRate(final FxRateGetLatestRequest r) {
        return Retrieves
                .doRequest(fxRateServiceWebClient,
                        FxRateService.GET_LATEST_FULL_PATH,
                        r,
                        NotFound.class,
                        LOG_NAME + "getLatestFxRate")
                .map(result -> (FxRateGetLatestResponse) result.getOptionalBody());
    }
}
