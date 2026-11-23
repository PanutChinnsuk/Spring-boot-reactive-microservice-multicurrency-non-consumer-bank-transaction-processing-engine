package com.panut.centralbanking.fxrateservice.api.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.fxrateservice.infrastructure.model.FxRate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FxRateMultiGetResponse {
    private Map<CurrencyCode, FxRateGetResponse> fxRateMap;
}
