package com.panut.centralbanking.transactionservice.api.service.account.response.queryresult;

import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.transactionservice.api.service.account.data.related.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfoForTransactionQueryResult {
    private String id;
    private CurrencyCode accountCurrency;
    private AccountStatus status;
}
