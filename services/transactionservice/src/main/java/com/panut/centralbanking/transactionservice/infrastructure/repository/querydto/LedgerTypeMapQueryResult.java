package com.panut.centralbanking.transactionservice.infrastructure.repository.querydto;

import com.panut.centralbanking.transactionservice.core.LedgerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LedgerTypeMapQueryResult {
    private String id;
    private LedgerType ledgerType;
    private final int size = 1;
}
