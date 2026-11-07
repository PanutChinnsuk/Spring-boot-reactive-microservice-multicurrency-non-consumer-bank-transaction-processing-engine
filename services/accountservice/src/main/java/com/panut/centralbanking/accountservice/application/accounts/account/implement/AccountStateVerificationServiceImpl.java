package com.panut.centralbanking.accountservice.application.account.implement;

import com.panut.centralbanking.accountservice.api.request.serviceonly.VerifyAccountsStateRequest;
import com.panut.centralbanking.accountservice.application.account.query.AccountQuery;
import com.panut.centralbanking.accountservice.application.account.service.AccountStateVerificationService;
import com.panut.centralbanking.accountservice.core.AccountStatus;
import com.panut.centralbanking.appcore.common.response.CommonResult;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class AccountStateVerificationServiceImpl implements
        AccountStateVerificationService {
    private final AccountQuery accountQuery;

    public AccountStateVerificationServiceImpl(AccountQuery accountQuery) {
        this.accountQuery = accountQuery;
    }

    public final AccountStatus ALLOWED_TRANSACTION_STATE = AccountStatus.ACTIVE;

    @Override
    public Mono<CommonResult> verifyAccountsState(final VerifyAccountsStateRequest r) {
        return accountQuery
                .findStateAccounts(r.getAccountIds(), ALLOWED_TRANSACTION_STATE)
                .thenReturn(CommonResult.defaultSuccess());
    }
}
