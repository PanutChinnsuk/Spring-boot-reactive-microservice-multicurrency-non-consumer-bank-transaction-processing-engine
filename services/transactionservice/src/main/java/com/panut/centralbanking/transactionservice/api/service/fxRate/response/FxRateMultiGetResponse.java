package com.panut.centralbanking.transactionservice.api.service.fxRate.response;

import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FxRateMultiGetResponse {
    private Map<CurrencyCode, FxRateGetResponse> fxRateMap;
}
