package com.panut.centralbanking.fxrateservice.api.controller.service;

import com.panut.centralbanking.appcore.common.response.CommonResult;
import com.panut.centralbanking.appcore.servicemaps.InternalServiceMapping;
import com.panut.centralbanking.appsecuritycore.servicesecurity.aspect.annotations.ServiceHasAuthorityExpectsMono;
import com.panut.centralbanking.fxrateservice.api.request.FxRateGetByIdRequest;
import com.panut.centralbanking.fxrateservice.api.request.FxRateGetLatestRequest;
import com.panut.centralbanking.fxrateservice.api.request.FxRateMultiGetLatestRequest;
import com.panut.centralbanking.fxrateservice.api.response.FxRateGetResponse;
import com.panut.centralbanking.fxrateservice.api.response.FxRateMultiGetResponse;
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
    public Mono<CommonResult<FxRateGetResponse>> getLatest(@RequestBody @Valid
                                                   FxRateGetLatestRequest r) {
        return fxRateService.getLatest(r);
    }

    @ServiceHasAuthorityExpectsMono(com.panut.centralbanking.appcore.servicemaps.map.FxRateService.MULTI_GET_LATEST_METHOD)
    @PostMapping(com.panut.centralbanking.appcore.servicemaps.map.FxRateService.MULTI_GET_LATEST_METHOD)
    public Mono<CommonResult<FxRateMultiGetResponse>> multiGetLatest(@RequestBody @Valid
                                                                     FxRateMultiGetLatestRequest r) {
        return fxRateService.multiGetLatest(r);
    }




    @ServiceHasAuthorityExpectsMono(com.panut.centralbanking.appcore.servicemaps.map.FxRateService.GET_BY_ID_METHOD)
    @PostMapping(com.panut.centralbanking.appcore.servicemaps.map.FxRateService.GET_BY_ID_METHOD)
    public Mono<CommonResult<FxRateGetResponse>> getById(@RequestBody @Valid
                                                               FxRateGetByIdRequest r) {
        return fxRateService.getById(r);
    }
}
