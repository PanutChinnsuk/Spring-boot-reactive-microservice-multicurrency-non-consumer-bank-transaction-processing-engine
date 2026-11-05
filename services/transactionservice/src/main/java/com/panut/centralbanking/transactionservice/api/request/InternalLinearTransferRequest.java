package com.panut.centralbanking.transactionservice.api.request;

import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.CustomAccountTransferDataPolicy;
import com.panut.centralbanking.transactionservice.application.transactions.pipelines.business.modules.InternalTransferPipeline.v1.data.CustomAccountTransferData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@NotNull
@AllArgsConstructor
@Data
public class InternalLinearTransferRequest {
    @NotNull
    @NotEmpty
    private List<@Valid CustomAccountTransferData> customDatas;

    private List<CustomAccountTransferDataPolicy> transferPolicies;
}
