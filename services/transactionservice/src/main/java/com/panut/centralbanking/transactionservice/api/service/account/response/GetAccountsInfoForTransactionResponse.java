package com.panut.centralbanking.transactionservice.api.service.account.response;


import com.panut.centralbanking.appcore.exceptions.BusinessException;
import com.panut.centralbanking.appcore.exceptions.SystemMisused;
import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.transactionservice.api.service.account.response.queryresult.AccountInfoForTransactionQueryResult;
import com.panut.centralbanking.transactionservice.api.service.account.data.related.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetAccountsInfoForTransactionResponse {
    private List<AccountInfoForTransactionQueryResult> queryResult;
    private List<AccountInfoForTransactionQueryResult> nonInternalQueryResult;
    private List<AccountInfoForTransactionQueryResult> internalQueryResult;

    private Stream<AccountInfoForTransactionQueryResult> streamed;

    private Map<String, AccountInfoForTransactionQueryResult> queryResultMap;


    public GetAccountsInfoForTransactionResponse(List<AccountInfoForTransactionQueryResult> queryResult) {
        this.queryResult = queryResult;
    }





    public AccountInfoForTransactionQueryResult getAccount(final String key) {
        if (queryResultMap == null)
            throw new SystemMisused("queryResultMap is null, due to calling getMapValue before " +
                    "compact.");

        return getMapValue(key, queryResultMap);
    }



    public GetAccountsInfoForTransactionResponse compact() {
        expectedNotEmpty();
        queryResultMap = getStream().collect(Collectors.toMap(
                AccountInfoForTransactionQueryResult::getId,
                Function.identity()
        ));


        return this;
    }

    public void sort() {
        internalQueryResult = getStream()
                        .filter(AccountInfoForTransactionQueryResult::isInternalAccount)
                        .toList();
        nonInternalQueryResult = getStream()
                .filter(AccountInfoForTransactionQueryResult::isNotInternalAccount)
                .toList();
    }

    public GetAccountsInfoForTransactionResponse expectedNotEmpty() {
        if (queryResult.isEmpty())
            throw new BusinessException("Empty accounts info for transaction.");
        return this;
    }

    public GetAccountsInfoForTransactionResponse expectedSize(final int size) {
        if (queryResult.size() != size)
            throw new BusinessException("Accounts info for transaction size not matched " +
                    "expected size of " + size);
        return this;
    }

    public GetAccountsInfoForTransactionResponse expectedAllAccountStatus(
            final AccountStatus expected) {
        if (getStream().anyMatch(query ->
                !query.getStatus().equals(expected)))
            throw new BusinessException("One of more accounts does not have expected " +
                    "account status of " + expected);

        return this;
    }

    public GetAccountsInfoForTransactionResponse expectedAllAccountStatusActive() {
        return expectedNoAccountStatus(AccountStatus.TERMINATED,
                AccountStatus.BLOCKED);
    }

    public GetAccountsInfoForTransactionResponse expectedSameCurrency() {
        CurrencyCode currencyCode = queryResult.getFirst().getAccountCurrency();

        if (getStream().anyMatch(query ->
                !query.getAccountCurrency().equals(currencyCode)))
            throw new BusinessException("One of more accounts does not have the same " +
                    "account currency of " + currencyCode);


        return this;

    }

    public GetAccountsInfoForTransactionResponse expectedAllNonInternalNotCurrency(
            final CurrencyCode notExpected) {
        if (getNonInternalStream().anyMatch(query ->
        query.getAccountCurrency().equals(notExpected)))
            throw new BusinessException("One or more non internal accounts have " +
                    "unexpected currency of " + notExpected);

        return this;
    }

    public GetAccountsInfoForTransactionResponse expectedBaseCurrencyInternalAccounts() {
        CurrencyCode base = CurrencyCode.getBaseCurrency();

        if (getInternalStream().anyMatch(query ->
                        !query.getAccountCurrency().equals(base)))
            throw new BusinessException("One or more internal accounts does not have base " +
                    "currency.");

        return this;
    }



    public GetAccountsInfoForTransactionResponse expectedNoAccountStatus(final AccountStatus... notExpected) {

        if (containAccountStatus(notExpected))
            throw new BusinessException("Accounts info for transaction contained account status that " +
                    "is unwanted.");

        return this;
    }

    public GetAccountsInfoForTransactionResponse expectedAccountStatus(final AccountStatus... expected) {

        if (!containAccountStatus(expected))
            throw new BusinessException("One or more accounts info does not contained account status that " +
                    "is wanted.");

        return this;
    }


    private boolean containAccountStatus(final AccountStatus... accountStatuses) {
        Set<AccountStatus> notExpectedSet = Set.of(accountStatuses);

        return getStream().anyMatch(query ->
                notExpectedSet.contains(query.getStatus()));
    }

    private Stream<AccountInfoForTransactionQueryResult> getStream() {
        return queryResult.stream();
    }

    private Stream<AccountInfoForTransactionQueryResult> getInternalStream() {
        if (internalQueryResult == null)
            throw new SystemMisused("internalQueryResult are null, due to calling " +
                    "before sort.");

        return internalQueryResult.stream();
    }

    private Stream<AccountInfoForTransactionQueryResult> getNonInternalStream() {
        if (nonInternalQueryResult == null)
            throw new SystemMisused("nonInternalQueryResult are null, due to calling " +
                    "before sort.");

        return nonInternalQueryResult.stream();
    }

    private AccountInfoForTransactionQueryResult getMapValue(final String key,
                                                             final Map<String, AccountInfoForTransactionQueryResult>
                                                                     accountsInfoQueryResultList) {
        AccountInfoForTransactionQueryResult value = accountsInfoQueryResultList.get(key);
        if (value == null)
            throw new BusinessException("Account info required for transaction not found from " +
                    "accountsInfoQueryResultList.");

        return value;
    }

}
