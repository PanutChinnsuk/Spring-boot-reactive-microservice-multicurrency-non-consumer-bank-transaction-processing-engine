package com.panut.centralbanking.fxrateservice.api.request;

import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FxRateCreateRequest {
    @NotNull
    private CurrencyCode baseCurrency;

    @NotNull
    private CurrencyCode quoteCurrency;

    @Positive
    @NotNull
    private BigDecimal bidRate;

    @Positive
    @NotNull
    private BigDecimal askRate;

    @Positive
    @NotNull
    private BigDecimal treasuryRate;
}
