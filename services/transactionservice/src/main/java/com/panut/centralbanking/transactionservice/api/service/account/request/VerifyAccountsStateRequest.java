package com.panut.centralbanking.transactionservice.api.service.account.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class VerifyAccountsStateRequest {
    private Set<String> accountIds;
}
