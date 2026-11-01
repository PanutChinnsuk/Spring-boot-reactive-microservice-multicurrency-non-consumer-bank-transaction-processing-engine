package com.panut.centralbanking.transactionservice.integration.transaction.utils;

import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.transactionservice.api.service.account.data.related.AccountStatus;
import com.panut.centralbanking.transactionservice.api.service.account.response.GetAccountsInfoForTransactionResponse;
import com.panut.centralbanking.transactionservice.api.service.account.response.queryresult.AccountInfoForTransactionQueryResult;

import java.util.List;


public class InternalDataBuilder {


    public static GetAccountsInfoForTransactionResponse validFxAccountsInfo(
            CurrencyCode quoteCurrency
    ) {
        List<AccountInfoForTransactionQueryResult> queryResults =
                List.of(
                        new AccountInfoForTransactionQueryResult("TEST",
                                quoteCurrency, AccountStatus.ACTIVE,
                                null),
                        new AccountInfoForTransactionQueryResult("TEST2",
                                CurrencyCode.getBaseCurrency(), AccountStatus.ACTIVE,
                                null),
                        new AccountInfoForTransactionQueryResult("INTERNAL",
                                CurrencyCode.getBaseCurrency(), AccountStatus.ACTIVE,
                                "INTERNAL"),
                        new AccountInfoForTransactionQueryResult("INTERNAL2",
                                CurrencyCode.getBaseCurrency(), AccountStatus.ACTIVE,
                                "INTERNAL2")
                );

        return new GetAccountsInfoForTransactionResponse(queryResults);
    }





}
