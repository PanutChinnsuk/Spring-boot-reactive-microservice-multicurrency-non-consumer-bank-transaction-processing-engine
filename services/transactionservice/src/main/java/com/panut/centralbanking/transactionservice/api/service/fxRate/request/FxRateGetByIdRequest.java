package com.panut.centralbanking.transactionservice.api.service.fxRate.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FxRateGetByIdRequest {
    private String fxRateId;


}
