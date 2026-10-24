package com.panut.centralbanking.accountservice.api.response.serviceonly;

import com.panut.centralbanking.accountservice.infrastructure.repository.queryresult.AccountInfoForTransactionQueryResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetAccountsInfoForTransactionResponse {
    private List<AccountInfoForTransactionQueryResult> queryResult;
}
