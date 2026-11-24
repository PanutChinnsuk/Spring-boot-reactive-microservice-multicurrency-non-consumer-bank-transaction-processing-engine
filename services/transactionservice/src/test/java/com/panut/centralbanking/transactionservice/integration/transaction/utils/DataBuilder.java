package com.panut.centralbanking.transactionservice.integration.transaction.utils;

import com.panut.centralbanking.appcore.integrationtest.SeededDatabase;
import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.transactionservice.api.service.account.data.related.AccountStatus;
import com.panut.centralbanking.transactionservice.api.service.account.response.GetAccountsInfoForTransactionResponse;
import com.panut.centralbanking.transactionservice.api.service.account.response.queryresult.AccountInfoForTransactionQueryResult;
import com.panut.centralbanking.transactionservice.api.service.fxRate.response.FxRateGetResponse;
import com.panut.centralbanking.transactionservice.api.service.fxRate.response.FxRateMultiGetResponse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class DataBuilder {
    public static String account1 = SeededDatabase.ID_NORMAL_ACCOUNT_1;
    public static String account2 = SeededDatabase.ID_NORMAL_ACCOUNT_2;

    public static String emptyAccount1 = SeededDatabase.ID_EMPTY_ACCOUNT_1;
    public static String emptyAccount2 = SeededDatabase.ID_EMPTY_ACCOUNT_2;
    public static String emptyAccount3 = SeededDatabase.ID_EMPTY_ACCOUNT_3;
    public static String emptyAccount4 = SeededDatabase.ID_EMPTY_ACCOUNT_4;
    public static String emptyAccount5 = SeededDatabase.ID_EMPTY_ACCOUNT_5;
    public static String emptyAccount6 = SeededDatabase.ID_EMPTY_ACCOUNT_6;

    public static String internalAccount1 = SeededDatabase.ID_INTERNAL_ACCOUNT_1;
    public static String internalAccount2 = SeededDatabase.ID_INTERNAL_ACCOUNT_2;

    public static String fxTHB = SeededDatabase.ID_FX_RATE_USD_THB;
    public static String fxCAD = SeededDatabase.ID_FX_RATE_USD_CAD;
    public static String fxAED = SeededDatabase.ID_FX_RATE_USD_AED;

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
                        new AccountInfoForTransactionQueryResult(account1,
                                CurrencyCode.getBaseCurrency(), AccountStatus.ACTIVE,
                                null),
                        new AccountInfoForTransactionQueryResult(account2,
                                CurrencyCode.getBaseCurrency(), AccountStatus.ACTIVE,
                                null)
                );

        return new GetAccountsInfoForTransactionResponse(queryResults);
    }

    public static FxRateGetResponse latestConversion(CurrencyCode quouteCurrency) {
        return new FxRateGetResponse(account1,
                BigDecimal.valueOf(25),
                BigDecimal.valueOf(50),
                BigDecimal.valueOf(37.5),
                BigDecimal.valueOf(40),
                CurrencyCode.getBaseCurrency(),
                quouteCurrency);

    }

    public static FxRateGetResponse latestConversion() {
        return latestConversion(null);

    }

    public static FxRateMultiGetResponse singleMultiGetLatestConversions(CurrencyCode quote) {
        return new FxRateMultiGetResponse(
                Map.of(quote, latestConversion())
        );

    }

    public static FxRateMultiGetResponse multiGetLatestConversions(CurrencyCode quote1,
                                                                   CurrencyCode quote2,
                                                                   CurrencyCode quote3) {
        return new FxRateMultiGetResponse(
                Map.of(quote1, new FxRateGetResponse(fxCAD,
                                BigDecimal.valueOf(5.21),
                                BigDecimal.valueOf(10.95),
                                BigDecimal.valueOf(6.214),
                                BigDecimal.valueOf(7),
                                CurrencyCode.getBaseCurrency(),
                                quote1),
                        quote2, latestConversion(),
                        quote3, new FxRateGetResponse(fxAED,
                                BigDecimal.valueOf(104.721),
                                BigDecimal.valueOf(155.125),
                                BigDecimal.valueOf(175.214),
                                BigDecimal.valueOf(180.251),
                                CurrencyCode.getBaseCurrency(),
                                quote1))
        );

    }


    public static GetAccountsInfoForTransactionResponse terminatedAccountsInfo() {
        List<AccountInfoForTransactionQueryResult> queryResults =
                List.of(
                        new AccountInfoForTransactionQueryResult(account1,
                                CurrencyCode.getBaseCurrency(), AccountStatus.ACTIVE,
                                null),
                        new AccountInfoForTransactionQueryResult(account2,
                                CurrencyCode.getBaseCurrency(), AccountStatus.TERMINATED,
                                null)
                );

        return new GetAccountsInfoForTransactionResponse(queryResults);
    }

    public static GetAccountsInfoForTransactionResponse differentCurrencyAccountsInfo() {
        List<AccountInfoForTransactionQueryResult> queryResults =
                List.of(
                        new AccountInfoForTransactionQueryResult(account1,
                                CurrencyCode.getBaseCurrency(), AccountStatus.ACTIVE,
                                null),
                        new AccountInfoForTransactionQueryResult(account2,
                                CurrencyCode.CAD, AccountStatus.ACTIVE,
                                null)
                );

        return new GetAccountsInfoForTransactionResponse(queryResults);
    }

}
