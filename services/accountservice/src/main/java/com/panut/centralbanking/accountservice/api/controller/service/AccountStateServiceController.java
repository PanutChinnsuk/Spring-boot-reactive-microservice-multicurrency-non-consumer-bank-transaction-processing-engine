package com.panut.centralbanking.accountservice.api.controller.service;

import com.panut.centralbanking.accountservice.api.request.serviceonly.VerifyAccountsStateRequest;
import com.panut.centralbanking.accountservice.application.accounts.account.service.AccountStateVerificationService;
import com.panut.centralbanking.appcore.common.response.CommonResult;
import com.panut.centralbanking.appcore.servicemaps.InternalServiceMapping;
import com.panut.centralbanking.appcore.servicemaps.map.AccountService;
import com.panut.centralbanking.appsecuritycore.servicesecurity.aspect.annotations.ServiceHasAuthorityExpectsMono;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(InternalServiceMapping.API)
public class AccountStateServiceController {
    private final AccountStateVerificationService accountStateVerificationService;

    public AccountStateServiceController(AccountStateVerificationService accountStateVerificationService) {
        this.accountStateVerificationService = accountStateVerificationService;
    }

    @ServiceHasAuthorityExpectsMono(AccountService.ACCOUNTS_STATE_VERIFY_METHOD)
    @PostMapping(AccountService.ACCOUNTS_STATE_VERIFY_METHOD)
    public Mono<CommonResult> verifyAccountsState(@RequestBody @Valid
                                                  VerifyAccountsStateRequest r) {
        return accountStateVerificationService.verifyAccountsState(r);
    }
}
