package com.panut.centralbanking.transactionservice.api.service.account.response;


import com.panut.centralbanking.appcore.exceptions.BusinessException;
import com.panut.centralbanking.appcore.exceptions.SystemMisused;
import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.transactionservice.api.service.account.response.queryresult.AccountsInfoForTransactionQueryResult;
import com.panut.centralbanking.transactionservice.core.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetAccountsInfoForTransactionResponse {
    private List<AccountsInfoForTransactionQueryResult> queryResult;
    private Stream<AccountsInfoForTransactionQueryResult> streamed;
    private Map<String, AccountsInfoForTransactionQueryResult> queryResultMap;


    private void toStream() {
        if (streamed == null) streamed = queryResult.stream();
    }


    private Stream<AccountsInfoForTransactionQueryResult> getStream() {
        toStream();

        return streamed;
    }

    public AccountsInfoForTransactionQueryResult getMapValue(final String key) {
        if (queryResultMap == null)
            throw new SystemMisused("queryResultMap is null, due to calling getMapValue before " +
                    "compact.");

        AccountsInfoForTransactionQueryResult value = queryResultMap.get(key);
        if (value == null)
            throw new BusinessException("Account info required for transaction not found from " +
                    "queryResultMap.");

        return value;
    }

    public GetAccountsInfoForTransactionResponse compact() {
        expectedNotEmpty();
        toStream();
        queryResultMap = getStream().collect(Collectors.toMap(
                queryResult -> queryResult.getId(),
                queryResult -> queryResult
        ));

        return this;
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
        return expectedNoAccountStatus(AccountStatus.ACTIVE);
    }

    public GetAccountsInfoForTransactionResponse expectedSameCurrency() {
        CurrencyCode currencyCode = queryResult.getFirst().getAccountCurrency();

        if (getStream().anyMatch(query ->
                !query.getAccountCurrency().equals(currencyCode)))
            throw new BusinessException("One of more accounts does not have the same " +
                    "account currency of " + currencyCode);


        return this;

    }

    public GetAccountsInfoForTransactionResponse expectedNoAccountStatus(final AccountStatus... notExpected) {
        Set<AccountStatus> notExpectedSet = Set.of(notExpected);
        if (getStream().anyMatch(query ->
                notExpectedSet.contains(query)))
            throw new BusinessException("Accounts info for transaction contained account status that " +
                    "is unwanted.");

        return this;
    }

}
