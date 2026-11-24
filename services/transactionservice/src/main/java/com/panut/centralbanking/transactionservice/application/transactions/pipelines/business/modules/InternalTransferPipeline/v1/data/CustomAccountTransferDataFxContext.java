package com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.v1.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.policies.CustomPerAccountFxTransferDataPolicy;
import com.panut.centralbanking.transactionservice.core.FxDirection;
import jakarta.annotation.Nullable;
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

    @Nullable
    private String targetFxAccountCurrencyId;

    @JsonIgnore
    public CustomAccountTransferDataFxContext(FxDirection directionForFx,
                                              CustomPerAccountFxTransferDataPolicy policy) {
        this.directionForFx = directionForFx;
        this.policy = policy;
    }



}
