package com.panut.centralbanking.fxrateservice.api.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.panut.centralbanking.appcurrencycore.CurrencyCode;
import com.panut.centralbanking.fxrateservice.infrastructure.model.FxRate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FxRateCreatedResponse {
    @JsonIgnore
    private String fxRateId;

    private CurrencyCode baseCurrency;
    private CurrencyCode quoteCurrency;
    private BigDecimal bidRate;
    private BigDecimal askRate;
    private BigDecimal midRate;
    private BigDecimal treasuryRate;
    private LocalDateTime timestamp;

    @JsonIgnore
    public static FxRateCreatedResponse toResponse(final FxRate fxRate) {
        return new FxRateCreatedResponse(
                fxRate.getId(),
                fxRate.getBaseCurrency(),
                fxRate.getQuoteCurrency(),
                fxRate.getBidRate(),
                fxRate.getAskRate(),
                fxRate.getMidRate(),
                fxRate.getTreasuryRate(),
                fxRate.getTimestamp()
        );
    }
}
