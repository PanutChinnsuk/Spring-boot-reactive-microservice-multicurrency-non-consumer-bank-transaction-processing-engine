package com.panut.centralbanking.accountservice.integration.internalaccounts;

import com.panut.centralbanking.accountservice.IntegrationDevTest;
import com.panut.centralbanking.accountservice.api.request.serviceonly.transactionprimary.GetAccountsInfoForTransactionRequest;
import com.panut.centralbanking.accountservice.api.response.serviceonly.GetAccountsInfoForTransactionResponse;
import com.panut.centralbanking.accountservice.api.response.serviceonly.GetFxInternalAccountResponse;
import com.panut.centralbanking.accountservice.application.account.service.AccountInfoService;
import com.panut.centralbanking.accountservice.application.internalaccounts.accounts.service.InternalAccountQueryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

public class InternalAccountQueryServiceTest implements IntegrationDevTest {
    @Autowired
    private InternalAccountQueryService internalAccountQueryService;

    @Autowired
    private AccountInfoService accountInfoService;


    @Test
    public void shouldGet() {
        GetFxInternalAccountResponse response = internalAccountQueryService
                .getFxInternalAccount().block();

        String clearingAccountId = response.getFxClearingInternalAccountId();
        String profitAccountId = response.getFxProfitInternalAccountId();


        Assertions.assertNotNull(clearingAccountId);
        Assertions.assertNotNull(profitAccountId);
        Assertions.assertNotEquals(clearingAccountId, profitAccountId);
    }

    @Test
    public void shouldApplyInternalMarker() {
        String internalAccountId = "INTERNAL";
        String nonInternalAccountId = "TEST";
        GetAccountsInfoForTransactionResponse withInternal = accountInfoService
                .getAccountInfosForTransaction(
                        new GetAccountsInfoForTransactionRequest(
                                Set.of(internalAccountId, nonInternalAccountId)
                        )
                ).block().getOptionalBody();



        withInternal.getQueryResult()
                .forEach(query -> {
                    if (query.getId().equals(internalAccountId))
                        Assertions.assertNotNull(query.getInternalId());

                    if (query.getId().equals(nonInternalAccountId))
                        Assertions.assertNull(query.getInternalId());
                });

    }
}
