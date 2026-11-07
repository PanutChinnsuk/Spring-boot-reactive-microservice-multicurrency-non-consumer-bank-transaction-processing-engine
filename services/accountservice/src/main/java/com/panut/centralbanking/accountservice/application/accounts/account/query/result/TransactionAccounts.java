package com.panut.centralbanking.accountservice.application.account.query.result;

import java.util.Map;
import java.util.Set;

public record TransactionAccounts(Set<String> accountIds,
                           Map<String,String> accountMap) {
}
