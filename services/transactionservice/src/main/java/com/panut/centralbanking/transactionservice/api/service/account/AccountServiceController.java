package com.panut.centralbanking.transactionservice.api.service.account;

import com.panut.centralbanking.appcore.client.Retrieves;
import com.panut.centralbanking.appcore.common.response.CommonResult;
import com.panut.centralbanking.appcore.exceptions.BusinessException;
import com.panut.centralbanking.appcore.servicemaps.map.AccountService;
import com.panut.centralbanking.transactionservice.api.service.account.request.GetAccountsInfoForTransactionRequest;
import com.panut.centralbanking.transactionservice.api.service.account.request.VerifyAccountsStateRequest;
import com.panut.centralbanking.transactionservice.api.service.account.response.GetAccountsInfoForTransactionResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class AccountServiceController {
    private final WebClient accountServiceWebClient;

    public AccountServiceController(WebClient accountServiceWebClient) {
        this.accountServiceWebClient = accountServiceWebClient;
    }

    private String LOG_NAME = "AccountService ";

    ParameterizedTypeReference<CommonResult<GetAccountsInfoForTransactionResponse>>
            getAccountsInfoForTransactionResponseType =
            new ParameterizedTypeReference<CommonResult<GetAccountsInfoForTransactionResponse>>() {};

    public Mono<CommonResult> verifyAccountStates(final VerifyAccountsStateRequest r) {
        return Retrieves.doRequest(accountServiceWebClient,
                AccountService.ACCOUNTS_STATE_VERIFY_FULL_PATH,
                r,
                BusinessException.class,
                LOG_NAME + "verifyAccountStates");
    }

    public Mono<GetAccountsInfoForTransactionResponse> getAccountsInfoForTransaction(
            final GetAccountsInfoForTransactionRequest r) {
        return Retrieves.doRequest(accountServiceWebClient,
                AccountService.ACCOUNTS_INFO_FOR_TRANSACTION_FULL_PATH,
                r,
                BusinessException.class,
                LOG_NAME + "getAccountsInfoForTransaction",
                        getAccountsInfoForTransactionResponseType)
                .map(result -> result.getOptionalBody());
    }


}
