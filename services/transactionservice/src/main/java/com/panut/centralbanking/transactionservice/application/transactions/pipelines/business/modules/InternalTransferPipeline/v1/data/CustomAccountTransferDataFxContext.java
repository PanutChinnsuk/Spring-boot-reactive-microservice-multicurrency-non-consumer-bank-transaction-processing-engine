package com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.v1.data;

import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.CustomPerAccountFxTransferDataPolicy;
import com.panut.centralbanking.transactionservice.core.FxDirection;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomAccountTransferDataFxContext {
    private FxDirection directionForFx = FxDirection.NONE;

    @NotNull
    private CustomPerAccountFxTransferDataPolicy policy;


}
