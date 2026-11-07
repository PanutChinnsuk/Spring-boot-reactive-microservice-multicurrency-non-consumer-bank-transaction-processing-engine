package com.panut.centralbanking.accountservice.api.controller.service;

import com.panut.centralbanking.accountservice.api.response.serviceonly.GetFxInternalAccountResponse;
import com.panut.centralbanking.accountservice.application.accounts.internalaccounts.accounts.service.InternalAccountQueryService;
import com.panut.centralbanking.appcore.servicemaps.InternalServiceMapping;
import com.panut.centralbanking.appcore.servicemaps.map.InternalAccountsService;
import com.panut.centralbanking.appsecuritycore.servicesecurity.aspect.annotations.ServiceHasAuthorityExpectsMono;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(InternalServiceMapping.API)
public class InternalAccountQueryServiceController {
    private final InternalAccountQueryService internalAccountQueryService;

    public InternalAccountQueryServiceController(InternalAccountQueryService internalAccountQueryService) {
        this.internalAccountQueryService = internalAccountQueryService;
    }

    @ServiceHasAuthorityExpectsMono(InternalAccountsService.GET_FX_INTERNAL_ACCOUNTS_METHOD)
    @GetMapping(InternalAccountsService.GET_FX_INTERNAL_ACCOUNTS_METHOD)
    public Mono<GetFxInternalAccountResponse> getFxInternalAccounts() {
        return internalAccountQueryService.getFxInternalAccount();
    }
}
