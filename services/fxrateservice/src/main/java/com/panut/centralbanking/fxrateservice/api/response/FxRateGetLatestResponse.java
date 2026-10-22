package com.panut.centralbanking.fxrateservice.api.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.fxrateservice.infrastructure.model.FxRate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FxRateGetLatestResponse {
    private String fxRateId;
    private BigDecimal bidRate;
    private BigDecimal askRate;
    private BigDecimal midRate;
    private BigDecimal treasuryRate;
    private CurrencyCode baseCurrency;



    @JsonIgnore
    public static FxRateGetLatestResponse toResponse(final FxRate fxRate) {
        return new FxRateGetLatestResponse(
                fxRate.getId(),
                fxRate.getBidRate(),
                fxRate.getAskRate(),
                fxRate.getMidRate(),
                fxRate.getTreasuryRate(),
                fxRate.getBaseCurrency()
        );
    }
}
