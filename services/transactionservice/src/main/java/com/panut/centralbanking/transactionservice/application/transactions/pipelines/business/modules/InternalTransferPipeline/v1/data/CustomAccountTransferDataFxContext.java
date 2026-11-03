package com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.v1.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.CustomPerAccountFxTransferDataPolicy;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.creation.register.data.pre.FxRateData;
import com.panut.centralbanking.transactionservice.core.FxDirection;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomAccountTransferDataFxContext {
    private FxDirection directionForFx = FxDirection.NONE;

    @NotNull
    private CustomPerAccountFxTransferDataPolicy policy;




}
