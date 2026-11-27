package com.panut.centralbanking.transactionservice.application.kafkalistener.data;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class JustProcessedBalanceChangeEvents {
    private Set<String> justProcessedIds;
}
