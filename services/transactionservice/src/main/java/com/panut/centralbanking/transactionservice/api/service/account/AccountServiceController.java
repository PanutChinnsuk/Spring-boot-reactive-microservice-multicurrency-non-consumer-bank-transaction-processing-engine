package com.panut.centralbanking.transactionservice.api.service.account;

import com.panut.centralbanking.appcore.client.Retrieves;
import com.panut.centralbanking.appcore.common.response.CommonResult;
import com.panut.centralbanking.appcore.exceptions.BusinessException;
import com.panut.centralbanking.appcore.servicemaps.map.AccountService;
import com.panut.centralbanking.transactionservice.api.service.account.request.VerifyAccountsStateRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AccountServiceController {
    private final WebClient accountServiceWebClient;

    public AccountServiceController(WebClient accountServiceWebClient) {
        this.accountServiceWebClient = accountServiceWebClient;
    }

    private String LOG_NAME = "AccountService ";

    public Mono<CommonResult> verifyAccountStates(final VerifyAccountsStateRequest r) {
        return Retrieves.doRequest(accountServiceWebClient,
                AccountService.ACCOUNTS_STATE_VERIFY_FULL_PATH,
                r,
                BusinessException.class,
                LOG_NAME + "verifyAccountStates");
    }
}
