package com.panut.centralbanking.accountservice.infrastructure.repository.queryresult;

import com.panut.centralbanking.accountservice.core.AccountStatus;
import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountsInfoForTransactionQueryResult {
    private String id;
    private CurrencyCode accountCurrency;
    private AccountStatus status;
}
