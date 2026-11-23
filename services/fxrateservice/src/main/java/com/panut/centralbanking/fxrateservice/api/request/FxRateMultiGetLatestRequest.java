package com.panut.centralbanking.fxrateservice.api.request;

import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FxRateMultiGetLatestRequest {
    @NotNull
    @NotEmpty
    private Set<CurrencyCode> baseCurrencies;

    @NotNull
    @NotEmpty
    private Set<CurrencyCode> quoteCurrencies;


}
