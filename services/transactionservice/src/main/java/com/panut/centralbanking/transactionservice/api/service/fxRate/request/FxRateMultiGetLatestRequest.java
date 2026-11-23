package com.panut.centralbanking.transactionservice.api.service.fxRate.request;

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
    private Set<CurrencyCode> baseCurrencies;
    private Set<CurrencyCode> quoteCurrencies;


}
