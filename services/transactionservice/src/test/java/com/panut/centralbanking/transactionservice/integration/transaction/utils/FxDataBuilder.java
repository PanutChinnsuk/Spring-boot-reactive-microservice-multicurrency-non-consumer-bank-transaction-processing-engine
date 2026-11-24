package com.panut.centralbanking.transactionservice.integration.transaction.utils;

import com.panut.centralbanking.appcore.integrationtest.SeededDatabase;
import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.transactionservice.api.service.account.data.related.AccountStatus;
import com.panut.centralbanking.transactionservice.api.service.account.response.GetAccountsInfoForTransactionResponse;
import com.panut.centralbanking.transactionservice.api.service.account.response.queryresult.AccountInfoForTransactionQueryResult;

import java.util.List;


public class FxDataBuilder {


    public static GetAccountsInfoForTransactionResponse foreignBuyBaseAccountsInfo(
            CurrencyCode quoteCurrency
    ) {
        List<AccountInfoForTransactionQueryResult> queryResults =
                List.of(
                        new AccountInfoForTransactionQueryResult(DataBuilder.account1,
                                quoteCurrency, AccountStatus.ACTIVE,
                                null),
                        new AccountInfoForTransactionQueryResult(DataBuilder.account2,
                                CurrencyCode.getBaseCurrency(), AccountStatus.ACTIVE,
                                null),
                        new AccountInfoForTransactionQueryResult(DataBuilder.internalAccount1,
                                CurrencyCode.getBaseCurrency(), AccountStatus.ACTIVE,
                                DataBuilder.internalAccount1),
                        new AccountInfoForTransactionQueryResult(DataBuilder.internalAccount2,
                                CurrencyCode.getBaseCurrency(), AccountStatus.ACTIVE,
                                DataBuilder.internalAccount2)
                );

        return new GetAccountsInfoForTransactionResponse(queryResults);
    }



    public static GetAccountsInfoForTransactionResponse baseBuyForeignAccountsInfo(
            CurrencyCode quoteCurrency
    ) {
        List<AccountInfoForTransactionQueryResult> queryResults =
                List.of(
                        new AccountInfoForTransactionQueryResult(DataBuilder.account1,
                                CurrencyCode.getBaseCurrency(), AccountStatus.ACTIVE,
                                null),
                        new AccountInfoForTransactionQueryResult(DataBuilder.account2,
                                quoteCurrency, AccountStatus.ACTIVE,
                                null),
                        new AccountInfoForTransactionQueryResult(DataBuilder.internalAccount1,
                                CurrencyCode.getBaseCurrency(), AccountStatus.ACTIVE,
                                DataBuilder.internalAccount1),
                        new AccountInfoForTransactionQueryResult(DataBuilder.internalAccount2,
                                CurrencyCode.getBaseCurrency(), AccountStatus.ACTIVE,
                                DataBuilder.internalAccount2)
                );

        return new GetAccountsInfoForTransactionResponse(queryResults);
    }



    public static GetAccountsInfoForTransactionResponse multiForeignBuyBaseAccountsInfo(
            CurrencyCode quote1,
            CurrencyCode quote2,
            CurrencyCode quote3
    ) {
        List<AccountInfoForTransactionQueryResult> queryResults =
                List.of(
                        new AccountInfoForTransactionQueryResult(DataBuilder.account1,
                                quote1, AccountStatus.ACTIVE,
                                null),
                        new AccountInfoForTransactionQueryResult(DataBuilder.account2,
                                quote2, AccountStatus.ACTIVE,
                                null),
                        new AccountInfoForTransactionQueryResult(DataBuilder.emptyAccount1,
                                quote3, AccountStatus.ACTIVE,
                                null),
                        new AccountInfoForTransactionQueryResult(DataBuilder.emptyAccount2,
                                CurrencyCode.getBaseCurrency(), AccountStatus.ACTIVE,
                                null),
                        new AccountInfoForTransactionQueryResult(DataBuilder.internalAccount1,
                                CurrencyCode.getBaseCurrency(), AccountStatus.ACTIVE,
                                DataBuilder.internalAccount1),
                        new AccountInfoForTransactionQueryResult(DataBuilder.internalAccount2,
                                CurrencyCode.getBaseCurrency(), AccountStatus.ACTIVE,
                                DataBuilder.internalAccount2)
                );

        return new GetAccountsInfoForTransactionResponse(queryResults);
    }


    public static GetAccountsInfoForTransactionResponse multiBaseBuyForeignAccountsInfo(
            CurrencyCode quote1,
            CurrencyCode quote2,
            CurrencyCode quote3
    ) {
        List<AccountInfoForTransactionQueryResult> queryResults =
                List.of(
                        new AccountInfoForTransactionQueryResult(DataBuilder.account1,
                                CurrencyCode.getBaseCurrency(), AccountStatus.ACTIVE,
                                null),
                        new AccountInfoForTransactionQueryResult(DataBuilder.account2,
                                CurrencyCode.getBaseCurrency(), AccountStatus.ACTIVE,
                                null),
                        new AccountInfoForTransactionQueryResult(DataBuilder.emptyAccount1,
                                CurrencyCode.getBaseCurrency(), AccountStatus.ACTIVE,
                                null),
                        new AccountInfoForTransactionQueryResult(DataBuilder.emptyAccount2,
                                quote1, AccountStatus.ACTIVE,
                                null),
                        new AccountInfoForTransactionQueryResult(DataBuilder.emptyAccount3,
                                quote2, AccountStatus.ACTIVE,
                                null),
                        new AccountInfoForTransactionQueryResult(DataBuilder.emptyAccount4,
                                quote3, AccountStatus.ACTIVE,
                                null),
                        new AccountInfoForTransactionQueryResult(DataBuilder.internalAccount1,
                                CurrencyCode.getBaseCurrency(), AccountStatus.ACTIVE,
                                DataBuilder.internalAccount1),
                        new AccountInfoForTransactionQueryResult(DataBuilder.internalAccount2,
                                CurrencyCode.getBaseCurrency(), AccountStatus.ACTIVE,
                                DataBuilder.internalAccount2)
                );

        return new GetAccountsInfoForTransactionResponse(queryResults);
    }


}
