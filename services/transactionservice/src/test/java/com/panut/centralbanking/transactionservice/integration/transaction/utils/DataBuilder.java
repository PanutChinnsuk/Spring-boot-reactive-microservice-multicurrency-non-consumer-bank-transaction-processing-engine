package com.panut.centralbanking.transactionservice.integration.transaction.utils;

import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.transactionservice.api.service.account.data.related.AccountStatus;
import com.panut.centralbanking.transactionservice.api.service.account.response.GetAccountsInfoForTransactionResponse;
import com.panut.centralbanking.transactionservice.api.service.account.response.queryresult.AccountInfoForTransactionQueryResult;
import org.springframework.stereotype.Component;

import java.util.List;


public class DataBuilder {


    public static GetAccountsInfoForTransactionResponse validAccountsInfo() {
        List<AccountInfoForTransactionQueryResult> queryResults =
                List.of(
                        new AccountInfoForTransactionQueryResult("TEST",
                                CurrencyCode.getBaseCurrency(), AccountStatus.ACTIVE,
                                false),
                        new AccountInfoForTransactionQueryResult("TEST2",
                                CurrencyCode.getBaseCurrency(), AccountStatus.ACTIVE,
                                false)
                );

        return new GetAccountsInfoForTransactionResponse(queryResults);
    }



    public static GetAccountsInfoForTransactionResponse terminatedAccountsInfo() {
        List<AccountInfoForTransactionQueryResult> queryResults =
                List.of(
                        new AccountInfoForTransactionQueryResult("TEST",
                                CurrencyCode.getBaseCurrency(), AccountStatus.ACTIVE,
                                false),
                        new AccountInfoForTransactionQueryResult("TEST2",
                                CurrencyCode.getBaseCurrency(), AccountStatus.TERMINATED,
                                false)
                );

        return new GetAccountsInfoForTransactionResponse(queryResults);
    }

    public static GetAccountsInfoForTransactionResponse differentCurrencyAccountsInfo() {
        List<AccountInfoForTransactionQueryResult> queryResults =
                List.of(
                        new AccountInfoForTransactionQueryResult("TEST",
                                CurrencyCode.getBaseCurrency(), AccountStatus.ACTIVE,
                                false),
                        new AccountInfoForTransactionQueryResult("TEST2",
                                CurrencyCode.CAD, AccountStatus.ACTIVE,
                                false)
                );

        return new GetAccountsInfoForTransactionResponse(queryResults);
    }

}
