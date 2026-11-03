package com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomInternalAccountProfitContext {
    @NotNull
    private BigDecimal foreignAmount;
}
