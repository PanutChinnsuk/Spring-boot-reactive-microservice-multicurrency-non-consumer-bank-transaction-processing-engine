package com.panut.centralbanking.transactionservice.infrastructure.repository.querydto;

import com.panut.centralbanking.transactionservice.core.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TransactionWithProfileQueryResult {
    private String transactionId;
    private TransactionStatus transactionStatus;
}
