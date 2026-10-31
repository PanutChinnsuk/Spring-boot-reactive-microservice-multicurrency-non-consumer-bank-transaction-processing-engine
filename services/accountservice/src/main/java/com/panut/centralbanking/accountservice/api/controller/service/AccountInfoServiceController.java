package com.panut.centralbanking.accountservice.api.controller.service;

import com.panut.centralbanking.accountservice.api.request.serviceonly.transactionprimary.GetAccountsInfoForTransactionRequest;
import com.panut.centralbanking.accountservice.api.response.serviceonly.GetAccountsInfoForTransactionResponse;
import com.panut.centralbanking.accountservice.application.account.service.AccountInfoService;
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
public class AccountInfoServiceController {
    private final AccountInfoService accountInfoService;

    public AccountInfoServiceController(AccountInfoService accountInfoService) {
        this.accountInfoService = accountInfoService;
    }


    @ServiceHasAuthorityExpectsMono(AccountService.ACCOUNTS_INFO_FOR_TRANSACTION_METHOD)
    @PostMapping(AccountService.ACCOUNTS_INFO_FOR_TRANSACTION_METHOD)
    public Mono<CommonResult<GetAccountsInfoForTransactionResponse>>
    getAccountsInfoForTransaction(@RequestBody @Valid GetAccountsInfoForTransactionRequest r) {
        return accountInfoService.getAccountInfosForTransaction(r);
    }
}
