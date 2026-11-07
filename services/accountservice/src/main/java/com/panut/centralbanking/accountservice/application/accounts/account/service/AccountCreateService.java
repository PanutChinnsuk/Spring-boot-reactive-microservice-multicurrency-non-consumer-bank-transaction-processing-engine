package com.panut.centralbanking.accountservice.application.accounts.account.service;

import com.panut.centralbanking.accountservice.api.data.AccountBalanceCreateData;
import com.panut.centralbanking.accountservice.api.data.AccountProfileCreateData;
import com.panut.centralbanking.accountservice.api.response.AccountCreatedData;
import reactor.core.publisher.Mono;

public interface AccountCreateService {
    Mono<AccountCreatedData> createAccount(String customerId,
                                           AccountProfileCreateData profileData,
                                           AccountBalanceCreateData balanceData);
}
