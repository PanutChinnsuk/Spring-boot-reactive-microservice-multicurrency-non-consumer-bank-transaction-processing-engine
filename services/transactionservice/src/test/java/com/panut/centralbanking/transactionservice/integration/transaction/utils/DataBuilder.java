package com.panut.centralbanking.transactionservice.integration.transaction.utils;

import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.transactionservice.api.service.account.data.related.AccountStatus;
import com.panut.centralbanking.transactionservice.api.service.account.response.GetAccountsInfoForTransactionResponse;
import com.panut.centralbanking.transactionservice.api.service.account.response.queryresult.AccountInfoForTransactionQueryResult;
import com.panut.centralbanking.transactionservice.api.service.fxRate.response.FxRateGetResponse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DataBuilder {

    public static GetAccountsInfoForTransactionResponse validAccountsInfo(String... accountIds) {
        List<AccountInfoForTransactionQueryResult> queryResults = new ArrayList<>();

        Arrays.stream(accountIds).forEach(id -> {
            queryResults.add(
                    new AccountInfoForTransactionQueryResult(id,
                            CurrencyCode.getBaseCurrency(), AccountStatus.ACTIVE,
                            null)
            );
        });

        return new GetAccountsInfoForTransactionResponse(queryResults);
    }

    public static GetAccountsInfoForTransactionResponse validAccountsInfo() {
        List<AccountInfoForTransactionQueryResult> queryResults =
                List.of(
                        new AccountInfoForTransactionQueryResult("TEST",
                                CurrencyCode.getBaseCurrency(), AccountStatus.ACTIVE,
                                null),
                        new AccountInfoForTransactionQueryResult("TEST2",
                                CurrencyCode.getBaseCurrency(), AccountStatus.ACTIVE,
                                null)
                );

        return new GetAccountsInfoForTransactionResponse(queryResults);
    }

    public static FxRateGetResponse latestConversion() {
        return new FxRateGetResponse("TEST",
                BigDecimal.valueOf(25),
                BigDecimal.valueOf(50),
                BigDecimal.valueOf(37.5),
                BigDecimal.valueOf(40),
                CurrencyCode.getBaseCurrency());

    }


    public static GetAccountsInfoForTransactionResponse terminatedAccountsInfo() {
        List<AccountInfoForTransactionQueryResult> queryResults =
                List.of(
                        new AccountInfoForTransactionQueryResult("TEST",
                                CurrencyCode.getBaseCurrency(), AccountStatus.ACTIVE,
                                null),
                        new AccountInfoForTransactionQueryResult("TEST2",
                                CurrencyCode.getBaseCurrency(), AccountStatus.TERMINATED,
                                null)
                );

        return new GetAccountsInfoForTransactionResponse(queryResults);
    }

    public static GetAccountsInfoForTransactionResponse differentCurrencyAccountsInfo() {
        List<AccountInfoForTransactionQueryResult> queryResults =
                List.of(
                        new AccountInfoForTransactionQueryResult("TEST",
                                CurrencyCode.getBaseCurrency(), AccountStatus.ACTIVE,
                                null),
                        new AccountInfoForTransactionQueryResult("TEST2",
                                CurrencyCode.CAD, AccountStatus.ACTIVE,
                                null)
                );

        return new GetAccountsInfoForTransactionResponse(queryResults);
    }

}
