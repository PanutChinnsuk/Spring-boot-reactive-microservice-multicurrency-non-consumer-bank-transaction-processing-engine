package com.panut.centralbanking.fxrateservice.api.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.fxrateservice.infrastructure.model.FxRate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FxRateGetResponse {
    private String fxRateId;
    private BigDecimal bidRate;
    private BigDecimal askRate;
    private BigDecimal midRate;
    private BigDecimal treasuryRate;
    private CurrencyCode baseCurrency;
    private CurrencyCode quoteCurrency;


    @JsonIgnore
    public static FxRateGetResponse toResponse(final FxRate fxRate) {
        return new FxRateGetResponse(
                fxRate.getId(),
                fxRate.getBidRate(),
                fxRate.getAskRate(),
                fxRate.getMidRate(),
                fxRate.getTreasuryRate(),
                fxRate.getBaseCurrency(),
                fxRate.getQuoteCurrency()
        );
    }
}
