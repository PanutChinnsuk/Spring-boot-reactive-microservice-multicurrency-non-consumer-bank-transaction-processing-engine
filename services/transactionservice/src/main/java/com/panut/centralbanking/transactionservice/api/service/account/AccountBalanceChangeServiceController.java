package com.panut.centralbanking.transactionservice.api.service.account;

import com.panut.centralbanking.appcore.client.Retrieves;
import com.panut.centralbanking.appcore.common.response.CommonResult;
import com.panut.centralbanking.appcore.exceptions.BusinessException;
import com.panut.centralbanking.appcore.servicemaps.map.AccountService;
import com.panut.centralbanking.transactionservice.api.service.account.request.GetAccountsInfoForTransactionRequest;
import com.panut.centralbanking.transactionservice.api.service.account.request.GetProcessedBalanceEventIdsRequest;
import com.panut.centralbanking.transactionservice.api.service.account.request.PerTransactionBalanceChangeRequest;
import com.panut.centralbanking.transactionservice.api.service.account.request.VerifyAccountsStateRequest;
import com.panut.centralbanking.transactionservice.api.service.account.response.GetAccountsInfoForTransactionResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Set;

@Service
public class AccountBalanceChangeServiceController {
    private final WebClient accountServiceWebClient;

    public AccountBalanceChangeServiceController(WebClient accountServiceWebClient) {
        this.accountServiceWebClient = accountServiceWebClient;
    }

    private String LOG_NAME = "AccountBalanceChangeService ";

    ParameterizedTypeReference<Set<String>> getAppliableBalanceEventsType =
            new ParameterizedTypeReference<Set<String>>() {};

    public Mono<CommonResult> perTransaction(final PerTransactionBalanceChangeRequest r) {
        return Retrieves.doRequest(accountServiceWebClient,
                AccountService.BALANCE_CHANGE_PER_TRANSACTION_FULL_PATH,
                r,
                BusinessException.class,
                LOG_NAME + "perTransaction");
    }

    public Mono<Set<String>> getAppliableBalanceEvents(final GetProcessedBalanceEventIdsRequest r) {
        return Retrieves.doRequestGenericParameterizedTypeReference(accountServiceWebClient,
                AccountService.GET_PROCESSED_BALANCE_CHANGE_EVENT_FULL_PATH,
                r,
                BusinessException.class,
                LOG_NAME + "getAppliableBalanceEvents",
                getAppliableBalanceEventsType);
    }




}
