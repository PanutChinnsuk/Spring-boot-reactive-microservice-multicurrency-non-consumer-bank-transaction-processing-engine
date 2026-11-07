package com.panut.centralbanking.accountservice.api.controller.service;

import com.panut.centralbanking.accountservice.api.request.serviceonly.transactionprimary.PerTransactionBalanceChangeRequest;
import com.panut.centralbanking.accountservice.application.transactions.accountbalancechange.service.BalanceChangeService;
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
public class AccountBalanceChangeServiceController {
    private final BalanceChangeService balanceChangeService;

    public AccountBalanceChangeServiceController(BalanceChangeService balanceChangeService) {
        this.balanceChangeService = balanceChangeService;
    }

    private final boolean REQUIRED_RESULT = false;

    @ServiceHasAuthorityExpectsMono(AccountService.BALANCE_CHANGE_PER_TRANSACTION_METHOD)
    @PostMapping(AccountService.BALANCE_CHANGE_PER_TRANSACTION_METHOD)
    public Mono<CommonResult> perTransaction(@RequestBody @Valid
                                             PerTransactionBalanceChangeRequest r) {
        return balanceChangeService.bulk(r.getBalanceChangeDatas(),
                REQUIRED_RESULT);
    }
}
