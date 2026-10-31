package com.panut.centralbanking.accountservice.infrastructure.repository.queryresult;

import com.panut.centralbanking.accountservice.core.InternalAccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InternalAccountQueryResult {
    private String accountId;
    private InternalAccountType internalAccountType;
}
