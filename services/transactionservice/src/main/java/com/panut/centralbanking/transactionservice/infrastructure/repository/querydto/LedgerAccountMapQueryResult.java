package com.panut.centralbanking.transactionservice.infrastructure.repository.querydto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LedgerAccountMapQueryResult {
    private String id;
    private String code;
}
