package com.panut.centralbanking.fxrateservice.api.request;

import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FxRateGetByIdRequest {
   @NotBlank
    private String fxRateId;


}
