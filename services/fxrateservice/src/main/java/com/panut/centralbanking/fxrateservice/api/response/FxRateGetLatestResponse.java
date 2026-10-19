package com.panut.centralbanking.fxrateservice.api.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private BigDecimal bidRate;
    private BigDecimal askRate;
    private BigDecimal midRate;
    private BigDecimal treasuryRate;



    @JsonIgnore
    public static FxRateGetLatestResponse toResponse(final FxRate fxRate) {
        return new FxRateGetLatestResponse(
                fxRate.getBidRate(),
                fxRate.getAskRate(),
                fxRate.getMidRate(),
                fxRate.getTreasuryRate()
        );
    }
}
