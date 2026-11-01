package com.panut.centralbanking.accountservice.integration.internalaccounts;

import com.panut.centralbanking.accountservice.IntegrationDevTest;
import com.panut.centralbanking.accountservice.api.response.serviceonly.GetFxInternalAccountResponse;
import com.panut.centralbanking.accountservice.application.internalaccounts.accounts.service.InternalAccountQueryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class InternalAccountQueryServiceTest implements IntegrationDevTest {
    @Autowired
    private InternalAccountQueryService internalAccountQueryService;


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
}
