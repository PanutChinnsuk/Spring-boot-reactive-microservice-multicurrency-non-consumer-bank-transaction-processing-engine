package com.panut.centralbanking.transactionservice.api.service.account.response.queryresult;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.panut.centralbanking.appcore.exceptions.BusinessException;
import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.transactionservice.api.service.account.data.related.AccountStatus;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfoForTransactionQueryResult {
    private String id;
    private CurrencyCode accountCurrency;
    private AccountStatus status;

    @Nullable
    private String internalId;



    @JsonIgnore
    public boolean isInternalAccount() {
        return internalId != null;
    }

    @JsonIgnore
    public boolean isNotInternalAccount() {
        return internalId == null;
    }

    @JsonIgnore
    public AccountInfoForTransactionQueryResult expectedStatus(final AccountStatus accountStatus) {
        if (!this.status.equals(accountStatus))
            throw new BusinessException("Account info within transaction does not have " +
                    "expected account status.");

        return this;
    }

    @JsonIgnore
    public AccountInfoForTransactionQueryResult expectedInternalAccount() {
        if (isNotInternalAccount())
            throw new BusinessException("Account info within transaction is not an " +
                    "internal account as wanted.");

        return this;
    }

    @JsonIgnore
    public AccountInfoForTransactionQueryResult expectedAccountCurrency(final CurrencyCode expected) {
        if (!this.accountCurrency.equals(expected))
            throw new BusinessException("Account info within transaction does not have " +
                    "expected account currency of " + expected);

        return this;
    }

    @JsonIgnore
    public AccountInfoForTransactionQueryResult notExpectedAccountCurrency(final CurrencyCode notExpected) {
        if (this.accountCurrency.equals(notExpected))
            throw new BusinessException("Account info within transaction have " +
                    "unexpected account currency of " + notExpected);

        return this;
    }

    @JsonIgnore
    public boolean isBaseCurrency() {
       return this.accountCurrency.equals(CurrencyCode.getBaseCurrency());
    }
}
