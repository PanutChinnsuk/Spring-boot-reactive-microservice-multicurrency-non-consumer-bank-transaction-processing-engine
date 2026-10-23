package com.panut.centralbanking.transactionservice.api.service.account.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetAccountsInfoForTransactionRequest {
    private Set<String> accountIds;
}
