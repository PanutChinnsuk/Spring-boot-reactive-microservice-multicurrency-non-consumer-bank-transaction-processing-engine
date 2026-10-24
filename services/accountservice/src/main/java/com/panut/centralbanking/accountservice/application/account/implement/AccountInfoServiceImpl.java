package com.panut.centralbanking.accountservice.application.account.implement;

import com.panut.centralbanking.accountservice.api.request.serviceonly.GetAccountsInfoForTransactionRequest;
import com.panut.centralbanking.accountservice.api.response.serviceonly.GetAccountsInfoForTransactionResponse;
import com.panut.centralbanking.accountservice.application.account.query.AccountQuery;
import com.panut.centralbanking.accountservice.application.account.service.AccountInfoService;
import com.panut.centralbanking.appcore.common.response.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class AccountInfoServiceImpl implements AccountInfoService {
    private final AccountQuery accountQuery;

    public AccountInfoServiceImpl(AccountQuery accountQuery) {
        this.accountQuery = accountQuery;
    }


    @Override
    public Mono<CommonResult<GetAccountsInfoForTransactionResponse>>
    getAccountInfosForTransaction(final GetAccountsInfoForTransactionRequest r) {
        return accountQuery.getAccountsInfoForTransaction(r.getAccountIds())
                .doOnNext(a ->
                        log.info("Get accounts info for transaction without throwing."))
                .map(list ->
                        new GetAccountsInfoForTransactionResponse(list))
                .doOnNext(a ->
                        log.info("Map query result to response"))
                .map(response ->
                        new CommonResult<GetAccountsInfoForTransactionResponse>(response))
                .doOnSuccess(a ->
                        log.info("Successfully get accounts info for transaction."))
                .doOnError(error ->
                        log.error("Failed to get accounts info for transaction, {}",
                                error.getLocalizedMessage()));
    }
}
