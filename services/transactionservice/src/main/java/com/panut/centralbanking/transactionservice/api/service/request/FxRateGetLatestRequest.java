package com.panut.centralbanking.transactionservice.api.service.request;

import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FxRateGetLatestRequest {
    private CurrencyCode baseCurrency;
    private CurrencyCode quoteCurrency;


}
