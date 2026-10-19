package com.panut.centralbanking.fxrateservice.api.controller.service;

import com.panut.centralbanking.appcore.servicemaps.InternalServiceMapping;
import com.panut.centralbanking.appsecuritycore.servicesecurity.aspect.annotations.ServiceHasAuthorityExpectsMono;
import com.panut.centralbanking.fxrateservice.api.request.FxRateGetLatestRequest;
import com.panut.centralbanking.fxrateservice.api.response.FxRateGetLatestResponse;
import com.panut.centralbanking.fxrateservice.application.service.FxRateService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(InternalServiceMapping.API)
public class FxRateServiceController {
    private final FxRateService fxRateService;

    public FxRateServiceController(FxRateService fxRateService) {
        this.fxRateService = fxRateService;
    }


    @ServiceHasAuthorityExpectsMono(com.panut.centralbanking.appcore.servicemaps.map.FxRateService.GET_LATEST_METHOD)
    @PostMapping(com.panut.centralbanking.appcore.servicemaps.map.FxRateService.GET_LATEST_METHOD)
    public Mono<FxRateGetLatestResponse> getLatest(@RequestBody @Valid
                                                   FxRateGetLatestRequest r) {
        return fxRateService.getLatest(r);
    }
}
